@file:JvmName("Main")

package io.ducommun.gameOfLife.jvm

import com.sun.javafx.collections.ObservableListWrapper
import io.ducommun.gameOfLife.BREEDER_1
import io.ducommun.gameOfLife.Plane
import io.ducommun.gameOfLife.TEN_ENGINE_CORDERSHIP_MK_ONE
import io.ducommun.gameOfLife.TEN_ENGINE_CORDERSHIP_MK_TWO
import io.ducommun.gameOfLife.parser.RunLengthEncodedParser
import javafx.application.Platform
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.ScatterChart
import javafx.scene.chart.XYChart
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import tornadofx.*
import java.io.File
import kotlin.reflect.KClass

fun main(args: Array<String>) = launch<GameOfLifeApp>(args)

val fps = 30L
val boardWidth = 700
val boardHeight = 700
val windowHeight = 1200.0
val windowWidth = windowHeight * (boardWidth.toDouble() / boardHeight)
val cellSize = windowHeight / boardHeight / 4
var xTranslation = 0.0
var yTranslation = 0.0

class PlaneWrapper : View() {

    val string = String(File("/Users/pivotal/workspace/game-of-life-multiplatform/game-of-life-jvm/src/main/resources/pattern.rle").readBytes())

    var plane = RunLengthEncodedParser().parseString(string)
//    var plane = BREEDER_1

    override val root = find<PlaneView>().root


    fun next(): Unit = runBlocking {

        val job = launch {
            plane = plane.next()
        }

        delay(1000 / fps)

        if (!job.isCompleted) {
            println("Running slow")
        }

        job.join()

        Platform.runLater {
            find<PlaneView>().update(newPlane = plane)
            next()
        }
    }

    init {

        importStylesheet(MyTestStylesheet::class)

        primaryStage.height = windowHeight
        primaryStage.width = windowWidth

        next()

    }
}

class PlaneView : View() {

    private val data1: ObservableListWrapper<XYChart.Data<Double, Double>> = ObservableListWrapper(ArrayList())
    private val data2: ObservableListWrapper<XYChart.Data<Double, Double>> = ObservableListWrapper(ArrayList())

    private val xDimension = boardWidth.toDouble() / 2
    val yDimension = boardHeight.toDouble() / 2

    private val xAxis = NumberAxis(-xDimension, xDimension, 0.0).apply {
        opacity = 0.0
    }
    private val yAxis = NumberAxis(-yDimension, yDimension, 0.0).apply {
        opacity = 0.0
    }

    private val listWrapper = ObservableListWrapper(listOf(
            XYChart.Series(data1) as XYChart.Series<Number, Number>,
            XYChart.Series(data2) as XYChart.Series<Number, Number>
    ))

    private val chart = ScatterChart(xAxis, yAxis, listWrapper).apply {
        setMinSize(windowWidth, windowHeight)
        animated = false
    }

    override val root = vbox {

        setMinSize(windowWidth, windowHeight)

        add(chart)
    }

    fun update(newPlane: Plane) {

        data1.clear()
//        data2.clear()
//        data1.addAll(TEN_ENGINE_CORDERSHIP_MK_ONE.toList().map { XYChart.Data(it.x + xTranslation, it.y + yTranslation) })
//        data2.addAll(TEN_ENGINE_CORDERSHIP_MK_TWO.toList().map { XYChart.Data(it.x + xTranslation, it.y + yTranslation) })
        data1.addAll(newPlane.toList().map { XYChart.Data(it.x + xTranslation, it.y + yTranslation) })

//        xTranslation += 1.0/12
//        yTranslation -= 1.0/12
    }
}

class GameOfLifeApp : App() {
    override val primaryView: KClass<out UIComponent> = PlaneWrapper::class
}

class MyTestStylesheet : Stylesheet() {
//            -fx-background-color: #e74c3c ;

    override fun render() = """

        .chart-symbol {
            -fx-background-radius: 0px ;
            -fx-padding: ${cellSize}px ;
        }

        .root {
            -fx-background: #94bfdc ;
        }

        .axis-tick-mark,
        .axis-minor-tick-mark,
        .chart-vertical-grid-lines,
        .chart-horizontal-grid-lines,
        .chart-vertical-zero-line,
        .chart-horizontal-zero-line  {
            -fx-stroke: transparent;
        }
        """
            .trimIndent()
}