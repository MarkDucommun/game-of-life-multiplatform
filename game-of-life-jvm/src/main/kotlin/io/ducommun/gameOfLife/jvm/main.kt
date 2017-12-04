@file:JvmName("Main")

package io.ducommun.gameOfLife.jvm

import com.sun.javafx.collections.ObservableListWrapper
import io.ducommun.gameOfLife.ACORN
import io.ducommun.gameOfLife.Plane
import javafx.application.Platform
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.ScatterChart
import javafx.scene.chart.XYChart
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import tornadofx.*
import kotlin.reflect.KClass

fun main(args: Array<String>) = launch<GameOfLifeApp>(args)

class PlaneWrapper : View() {

    var plane = ACORN

    override val root = find<PlaneFragment>().root

    fun next(): Unit = runBlocking {

        val job = launch {
            plane = plane.next()
        }

        delay(150)

        if (!job.isCompleted) {
            println("Running slow")
        }

        job.join()

        Platform.runLater {
            find<PlaneFragment>().update(newPlane = plane)
            next()
        }
    }

    init {

        importStylesheet(MyTestStylesheet::class)

        primaryStage.height = 1000.0
        primaryStage.width = 1500.0

        repeat(3) {
            plane = plane.next()
        }

        next()

    }
}

class PlaneFragment : View() {

    private val data: ObservableListWrapper<XYChart.Data<Short, Short>> = ObservableListWrapper(ArrayList())

    private val dimension = 100.0

    private val xAxis = NumberAxis(-dimension, dimension, 0.0).apply {
        opacity = 0.0
    }
    private val yAxis = NumberAxis(-dimension, dimension, 0.0).apply {
        opacity = 0.0
    }

    private val listWrapper = ObservableListWrapper(listOf(XYChart.Series(data) as XYChart.Series<Number, Number>))

    override val root = vbox {

        add(ScatterChart(xAxis, yAxis, listWrapper).apply {
            setMinSize(1000.0, 1000.0)
        })
        this.setMinSize(1000.0, 1000.0)
    }

    fun update(newPlane: Plane) {

        data.clear()
        data.addAll(newPlane.toList().map { XYChart.Data(it.x, it.y) })
    }
}

class GameOfLifeApp : App() {
    override val primaryView: KClass<out UIComponent> = PlaneWrapper::class
}

class MyTestStylesheet : Stylesheet() {
    // No builder support, faking it for now :)
    override fun render() = """

        .chart-symbol {
            -fx-background-radius: 10px ;
            -fx-background-color: #e74c3c ;
            -fx-padding: 2.5px ;
            -fx-margin: 0px ;
        }

        .root {
            -fx-background: #2980b9 ;
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