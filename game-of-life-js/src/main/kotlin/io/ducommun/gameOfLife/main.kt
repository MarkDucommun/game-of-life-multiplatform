package io.ducommun.gameOfLife

import io.ducommun.gameOfLife.Presets.BREEDER_ONE
import io.ducommun.gameOfLife.Presets.EMPTY
import io.ducommun.gameOfLife.Presets.INFINITE_GLIDER_HOTEL_FOUR
import io.ducommun.gameOfLife.viewModel.GameOfLifeViewModel
import io.ducommun.gameOfLife.viewModel.Scheduler
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.clear
import kotlin.js.Date
import kotlin.math.floor

fun main(args: Array<String>) {

    val canvasDimension = 1024
    val boardDimension = 4096
    val plane = BREEDER_ONE
    val deadColor = Colors.FLAT_BLACK
    val aliveColor = Colors.LIGHT_YELLOW

    val title = document.createElement("div") as HTMLDivElement
    val canvas = document.createElement("canvas") as HTMLCanvasElement

    val deadColorString = "#${deadColor.asDynamic().toString(16)}"
    val aliveColorString = "#${aliveColor.asDynamic().toString(16)}"

    title.style.backgroundColor = deadColorString
    title.style.fontFamily = "andale mono"
    title.style.color = aliveColorString
    title.style.paddingLeft = "10px"
    title.style.paddingTop = "3px"

    canvas.width = canvasDimension
    canvas.height = canvasDimension

    document.body?.appendChild(title)
    document.body?.appendChild(canvas)
    document.body?.style?.backgroundColor = deadColorString

    val context = canvas.getContext("2d") as CanvasRenderingContext2D
    context.fillStyle = deadColorString
    context.fillRect(0.0, 0.0, canvasDimension.toDouble(), canvasDimension.toDouble())

    GameOfLifeViewModel(
        scheduler = BrowserScheduler(),
        canvasWidth = canvasDimension,
        canvasHeight = canvasDimension,
        initialBoardWidth = boardDimension,
        initialBoardHeight = boardDimension,
        aliveColor = 0xFF_00_00_00.toInt() + aliveColor,
        deadColor = 0xFF_00_00_00.toInt() + deadColor,
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
        onSetStats { stats ->
            title.clear()
            stats.run {
                val boardInfo = "$xDimension x $yDimension at (${origin.x},${origin.y})"
                val gameInfo = "$cellCount alive cells at generation $generation after ${elapsedSeconds / 1000} seconds running"
                val base = "$boardInfo :: $gameInfo"

                val message = if (running) "${floor(fps * 100) / 100} frames per second :: $base" else base

                title.append(message.toUpperCase())
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
                "r" -> setPlane(plane)
                "s" -> if (event.shiftKey) slowDown() else speedUp()
            }
        }
        )

        canvas.addEventListener(
            type = "click",
            callback = { rawEvent ->

                val event = rawEvent as MouseEvent

                toggle(canvasX = event.offsetX, canvasY = event.offsetY)
            }
        )

        setPlane(plane)
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