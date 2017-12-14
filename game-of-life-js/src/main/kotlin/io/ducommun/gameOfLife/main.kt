package io.ducommun.gameOfLife

import io.ducommun.gameOfLife.Presets.SPAGHETTI_MONSTER
import io.ducommun.gameOfLife.viewModel.GameOfLifeViewModel
import io.ducommun.gameOfLife.viewModel.Scheduler
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date

fun main(args: Array<String>) {

    val canvasDimension = 1024
    val boardDimension = 4096

    val canvas = document.createElement("canvas") as HTMLCanvasElement

    canvas.width = canvasDimension
    canvas.height = canvasDimension

    document.body?.appendChild(canvas)

    val context = canvas.getContext("2d") as CanvasRenderingContext2D
    context.fillStyle = "#C1D5EC"
    context.fillRect(0.0, 0.0, canvasDimension.toDouble(), canvasDimension.toDouble())

    GameOfLifeViewModel(
        scheduler = BrowserScheduler(),
        canvasWidth = canvasDimension,
        canvasHeight = canvasDimension,
        initialBoardWidth = boardDimension,
        initialBoardHeight = boardDimension,
        aliveColor = 0xFF_00_00_00.toInt() + Colors.LIGHT_YELLOW,
        deadColor = 0xFF_00_00_00.toInt() + Colors.FLAT_BLACK,
        initialFps = 120,
        useGradient = true
    ).run {

        onDraw { pixels ->
            context.run {
                val imageData = createImageData(canvasDimension.toDouble(), canvasDimension.toDouble())
                val bytes = Uint8ClampedArray(canvasDimension * canvasDimension * 4)
                pixels.forEachIndexed { index, pixel ->
                    bytes.asDynamic()[4 * index] = pixel.and(0xff0000).shr(16)
                    bytes.asDynamic()[4 * index + 1] = pixel.and(0xff00).shr(8)
                    bytes.asDynamic()[4 * index + 2] = pixel.and(0xff)
                    bytes.asDynamic()[4 * index + 3] = pixel.and(0xff000000L.toInt()).ushr(24)
                }
                imageData.data.set(bytes)
                putImageData(imageData, 0.0, 0.0)
            }
        }
        onDrawDiff { rects ->
            context.run {
                rects.forEach { rect ->
                    val color = rect.color.and(0xffffff).asDynamic().toString(16)
                    fillStyle = "#$color"
                    fillRect(rect.x.toDouble(), rect.y.toDouble(), rect.width.toDouble(), rect.height.toDouble())
                }
            }
        }
        onGetTimeMillis { Date().getTime().toLong() }

        document.addEventListener(
            type = "keydown", callback = { rawEvent ->

            val event = rawEvent as KeyboardEvent

            when (event.key) {
                "ArrowRight" -> panRight()
                "ArrowLeft" -> panLeft()
                "ArrowUp" -> panUp()
                "ArrowDown" -> panDown()
                "=" -> zoomIn()
                "-" -> zoomOut()
                "n" -> next()
                " " -> toggleRunning()
                "s" -> if (event.shiftKey) slowDown() else speedUp()
            }
        }
        )

        canvas.addEventListener(type = "click", callback = { rawEvent ->

            val event = rawEvent as MouseEvent

            toggle(canvasX = event.pageX.toDouble(), canvasY = event.pageY.toDouble())
        })

        setPlane(SPAGHETTI_MONSTER)
        start()
    }
}

class BrowserScheduler : Scheduler {

    override fun immediately(task: () -> Unit) {
        window.setTimeout(task, 0)
    }

    override fun delay(milliseconds: Int, task: () -> Unit) {
        window.setTimeout(task, milliseconds)
    }

    override fun onUIThread(task: () -> Unit) {
        window.setTimeout(task, 0)
    }
}