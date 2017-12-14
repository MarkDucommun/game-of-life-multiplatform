@file:JvmName("Main")

package io.ducommun.gameOfLife.jvm

import io.ducommun.gameOfLife.Coordinate
import io.ducommun.gameOfLife.Plane
import io.ducommun.gameOfLife.PlaneDiff
import io.ducommun.gameOfLife.Presets
import javafx.application.Platform
import javafx.scene.image.ImageView
import javafx.scene.image.PixelFormat
import javafx.scene.image.WritableImage
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import tornadofx.*
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

fun main(args: Array<String>) = launch<GameOfLifeApp>(args)

var fps = 60L

val windowHeight = 1024.0
val windowWidth = 1024.0

var boardWidth = 512
var boardHeight = 512

var xTranslation = 0
var yTranslation = 0

var generations = 0
var previousGenerations = 0
var actualFps = 0
var startTime: Long = 0

class PlaneWrapper : View() {

    private var plane = Presets.R_PENTOMINO

    override val root = vbox {
        add(find<PlaneView>())
    }

    private fun next(): Job = launch {

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
            title = titleString
            next()
        }

        generations += 1
    }

    private fun render() {

        Platform.runLater {
            find<PlaneView>().update(newPlane = plane)
            title = titleString
        }
    }

    private fun calculateFps(): Job = launch {
        actualFps = generations - previousGenerations
        previousGenerations = generations
        delay(time = 1, unit = TimeUnit.SECONDS)
        calculateFps()
    }

    private val titleString: String
        get() =
            "${boardWidth}x${boardHeight}, ${plane.size} living cells, generation ${generations}, ${actualFps} fps," +
            " at (${-xTranslation}, ${-yTranslation}), ${(System.currentTimeMillis() - startTime) / 1000}s elapsed"

    init {

        primaryStage.height = windowHeight
        primaryStage.width = windowWidth
        startTime = System.currentTimeMillis()

        render()

        next()

        calculateFps()

        shortcut("right") {
            xTranslation -= boardWidth / 100
            find<PlaneView>().update(plane)
        }
        shortcut("left") {
            xTranslation += boardWidth / 100
            find<PlaneView>().update(plane)
        }
        shortcut("up") {
            yTranslation += boardHeight / 100
            find<PlaneView>().update(plane)
        }
        shortcut("down") {
            yTranslation -= boardHeight / 100
            find<PlaneView>().update(plane)
        }
        shortcut("equals") {
            if (boardWidth >= 2 && boardHeight >= 2) {
                boardWidth /= 2
                boardHeight /= 2
                find<PlaneView>().update(plane)
            }
        }
        shortcut("minus") {
            if (boardWidth < windowWidth && boardHeight < windowHeight) {
                boardWidth *= 2
                boardHeight *= 2
            }
            find<PlaneView>().update(plane)
        }
        shortcut("n") {
            next()
        }
        shortcut("s") {
            fps += 1
        }
        shortcut("shift+s") {
            if (fps > 1) fps -= 1
        }
    }
}

class PlaneView : View() {

    private val image = WritableImage(windowWidth.toInt(), windowHeight.toInt())

    override val root = vbox {

        setMinSize(windowWidth, windowHeight)
        setMaxSize(windowWidth, windowHeight)

        add(ImageView(image))
    }

    private fun draw(plane: Plane) {

        val xDimension = boardWidth / 2
        val yDimension = boardHeight / 2
        val actualCellSize = (windowWidth.toInt() / boardWidth)
        val pixelWriter = image.pixelWriter

        clear()

        val aliveCell = IntArray(actualCellSize * actualCellSize) { 0xff7A3433L.toInt() }
        val deadCell = IntArray(actualCellSize * actualCellSize) { 0xffC1D5ECL.toInt() }

        for (x in -xDimension + xTranslation until xDimension + xTranslation) for (y in -yDimension + yTranslation until yDimension + yTranslation) {
            pixelWriter.setPixels(
                (x - xTranslation + xDimension) * actualCellSize,
                (y - yTranslation + yDimension) * actualCellSize,
                actualCellSize, actualCellSize,
                PixelFormat.getIntArgbInstance(),
                if (plane.alive(Coordinate(x.toShort(), 0.minus(y).toShort()))) aliveCell else deadCell,
                0, actualCellSize
            )
        }
    }

    private fun draw(diff: PlaneDiff) {

        val xDimension = boardWidth / 2
        val yDimension = boardHeight / 2
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
                0, actualCellSize
            )
        }

        for (coordinate in diff.kill.filter { coordinate -> -xDimension <= coordinate.x - xTranslation && coordinate.x - xTranslation < xDimension && -yDimension <= -coordinate.y - yTranslation && -coordinate.y - yTranslation < yDimension }) {
            pixelWriter.setPixels(
                (coordinate.x - xTranslation + xDimension) * actualCellSize,
                (-coordinate.y - yTranslation + yDimension) * actualCellSize,
                actualCellSize, actualCellSize,
                PixelFormat.getIntArgbInstance(),
                deadCell,
                0, actualCellSize
            )
        }
    }

    fun clear() {
        image.pixelWriter.setPixels(
            0, 0,
            windowWidth.toInt(), windowHeight.toInt(),
            PixelFormat.getIntArgbInstance(),
            emptyPlane,
            0, windowWidth.toInt()
        )
    }

    private val emptyPlane = IntArray(windowWidth.toInt() * windowHeight.toInt()) { 0xffC1D5ECL.toInt() }

    fun update(newPlane: Plane) {
        draw(newPlane)
    }

    fun update(diff: PlaneDiff) {
        draw(diff)
    }
}

class GameOfLifeApp : App() {
    override val primaryView: KClass<out UIComponent> = PlaneWrapper::class
}