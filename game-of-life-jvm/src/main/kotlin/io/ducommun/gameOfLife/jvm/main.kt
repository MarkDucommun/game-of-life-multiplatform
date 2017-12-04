@file:JvmName("Main")

package io.ducommun.gameOfLife.jvm

import io.ducommun.gameOfLife.Coordinate
import io.ducommun.gameOfLife.Plane
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import tornadofx.*
import kotlin.reflect.KClass

fun main(args: Array<String>) = launch<GameOfLifeApp>(args)

class PlaneWrapper : View() {

    override val root = vbox {
        setMaxSize(1000.0, 1000.0)
        useMaxSize = true
    }

    fun setState(plane: Plane) {

        root.replaceChildren {
            add(find<PlaneFragment>(params = mapOf<Any, Any?>(
                    PlaneFragment::plane to plane.next(),
                    PlaneFragment::next to ::setState
            )))
        }
    }

    init {

        root.replaceChildren {

            add(find<PlaneFragment>(params = mapOf<Any, Any?>(
                    PlaneFragment::plane to Plane(setOf(
                            Coordinate(x = 0, y = 1),
                            Coordinate(x = 0, y = 0),
                            Coordinate(x = 0, y = -1),
                            Coordinate(x = -1, y = 0),
                            Coordinate(x = 1, y = 1)
                    )),
                    PlaneFragment::next to ::setState
            )))
        }
    }
}

class PlaneFragment : Fragment() {

    val plane: Plane by param()

    val next: (Plane) -> Boolean by param()

    override val root = vbox {

        scatterchart(
                title = "",
                x = NumberAxis(-20.0, 20.0, 0.0),
                y = NumberAxis(-20.0, 20.0, 0.0)) {

            series(name = "test") {

                plane.forEach { data(x = it.x, y = it.y) }
            }
        }
    }

    init {

        runAsync {
            Thread.sleep(15)
        } ui {
            next(plane)
        }
    }
}

class GameOfLifeApp : App() {
    override val primaryView: KClass<out UIComponent> = PlaneWrapper::class
}