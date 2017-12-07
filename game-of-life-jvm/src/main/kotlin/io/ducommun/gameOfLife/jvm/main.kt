@file:JvmName("Main")

package io.ducommun.gameOfLife.jvm

import io.ducommun.gameOfLife.*
import io.ducommun.gameOfLife.parser.RunLengthEncodedParser
import javafx.application.Platform
import javafx.scene.image.ImageView
import javafx.scene.image.PixelFormat
import javafx.scene.image.WritableImage
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import tornadofx.*
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

fun main(args: Array<String>) = launch<GameOfLifeApp>(args)

val fps = 100L

val windowHeight = 1024.0
val windowWidth = 1024.0

var boardWidth = 512
var boardHeight = 512

var xTranslation = 0
var yTranslation = 0

var generations = 0
var previousGenerations = 0
var actualFps = 0

class PlaneWrapper : View() {

    val string = String(File("/Users/pivotal/workspace/game-of-life-multiplatform/game-of-life-jvm/src/main/resources/pattern.rle").readBytes())

//    var plane = BREEDER_1
    var plane = RunLengthEncodedParser().parseString(string)

    override val root = vbox {
        add(find<PlaneView>())
    }

    fun next(): Job = launch {

        var diff: PlaneDiff? = null

        val job = launch {
            diff = plane.nextDiff
            plane = plane.next()
        }

        delay(1000 / fps)

        if (!job.isCompleted) {
            println("Running slow")
        }

        job.join()

        Platform.runLater {
            find<PlaneView>().update(diff = diff ?: throw RuntimeException("uh oh"))
            title = "${boardWidth}x${boardHeight}, ${plane.size} living cells, generation ${generations}, ${actualFps} fps, at (${-xTranslation}, ${-yTranslation})"
            next()
        }

        generations += 1
    }

    fun render() {

        Platform.runLater {
            find<PlaneView>().update(newPlane = plane)
            title = "${boardWidth}x${boardHeight}, ${plane.size} living cells, generation ${generations}, ${actualFps} fps, at (${-xTranslation}, ${-yTranslation})"
        }
    }

    fun calculateFps(): Job = launch {
        actualFps = generations - previousGenerations
        previousGenerations = generations
        delay(1, TimeUnit.SECONDS)
        calculateFps()
    }

    init {

        primaryStage.height = windowHeight
        primaryStage.width = windowWidth

        render()

        next()

        calculateFps()

        shortcut("right") {
            xTranslation -= boardWidth / 100
            find<PlaneView>().draw(plane)
        }
        shortcut("left") {
            xTranslation += boardWidth / 100
            find<PlaneView>().draw(plane)
        }
        shortcut("up") {
            yTranslation += boardHeight / 100
            find<PlaneView>().draw(plane)
        }
        shortcut("down") {
            yTranslation -= boardHeight / 100
            find<PlaneView>().draw(plane)
        }
        shortcut("equals") {
            if (boardWidth >= 2 && boardHeight >= 2) {
                boardWidth /= 2
                boardHeight /= 2
                find<PlaneView>().draw(plane)
            }
        }
        shortcut("minus") {
            if (boardWidth < windowWidth && boardHeight < windowHeight) {
                boardWidth *= 2
                boardHeight *= 2
            }
            find<PlaneView>().draw(plane)
        }
        shortcut("n") {
            next()
        }
    }
}

class PlaneView : View() {

    private val image = WritableImage(windowWidth.toInt(), windowHeight.toInt())

    override val root = vbox {

        setMinSize(windowWidth, windowHeight)

        add(ImageView(image))
    }

    val emptyPlane = IntArray(windowWidth.toInt() * windowHeight.toInt()) { 0xffC1D5ECL.toInt() }

    fun draw(plane: Plane) {

        val xDimension = boardWidth / 2
        val yDimension = boardHeight /2
        val actualCellSize = (windowWidth.toInt() / boardWidth)
        val pixelWriter = image.pixelWriter

        pixelWriter.setPixels(
                0, 0,
                windowWidth.toInt(), windowHeight.toInt(),
                PixelFormat.getIntArgbInstance(),
                emptyPlane,
                0, windowWidth.toInt())

        val aliveCell = IntArray(actualCellSize * actualCellSize) { 0xff7A3433L.toInt() }
        val deadCell = IntArray(actualCellSize * actualCellSize) { 0xffC1D5ECL.toInt() }

        for (x in -xDimension + xTranslation until xDimension + xTranslation) for (y in -yDimension + yTranslation until yDimension + yTranslation) {
            pixelWriter.setPixels(
                    (x - xTranslation + xDimension) * actualCellSize,
                    (y - yTranslation + yDimension) * actualCellSize,
                    actualCellSize, actualCellSize,
                    PixelFormat.getIntArgbInstance(),
                    if (plane.alive(Coordinate(x.toShort(), 0.minus(y).toShort()))) aliveCell else deadCell,
                    0, actualCellSize)
        }
    }

    fun draw(diff: PlaneDiff) {

        val xDimension = boardWidth / 2
        val yDimension = boardHeight /2
        val actualCellSize = (windowWidth / boardWidth).toInt()
        val pixelWriter = image.pixelWriter

        val aliveCell = IntArray(actualCellSize * actualCellSize) { 0xff7A3433L.toInt() }
        val deadCell = IntArray(actualCellSize * actualCellSize) { 0xffC1D5ECL.toInt() }

        for (coordinate in diff.revive.filter { coordinate -> -xDimension <= coordinate.x - xTranslation && coordinate.x - xTranslation < xDimension && -yDimension <= -coordinate.y - yTranslation && -coordinate.y - yTranslation < yDimension }) {
            pixelWriter.setPixels(
                    (coordinate.x - xTranslation + xDimension) * actualCellSize,
                    (-coordinate.y - yTranslation + yDimension) * actualCellSize,
                    actualCellSize, actualCellSize,
                    PixelFormat.getIntArgbInstance(),
                    aliveCell,
                    0, actualCellSize)
        }

        for (coordinate in diff.kill.filter { coordinate -> -xDimension <= coordinate.x - xTranslation && coordinate.x - xTranslation < xDimension && -yDimension <= -coordinate.y - yTranslation && -coordinate.y - yTranslation < yDimension }) {
            pixelWriter.setPixels(
                    (coordinate.x - xTranslation + xDimension) * actualCellSize,
                    (-coordinate.y - yTranslation + yDimension) * actualCellSize,
                    actualCellSize, actualCellSize,
                    PixelFormat.getIntArgbInstance(),
                    deadCell,
                    0, actualCellSize)
        }
    }

    fun update(newPlane: Plane) {

        draw(newPlane)

//        xTranslation += xTranslationPerFrame
//        yTranslation += yTranslationPerFrame
    }

    fun update(diff: PlaneDiff) {
        draw(diff)
    }
}

class GameOfLifeApp : App() {
    override val primaryView: KClass<out UIComponent> = PlaneWrapper::class
}