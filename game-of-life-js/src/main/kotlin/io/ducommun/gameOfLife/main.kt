package io.ducommun.gameOfLife

import io.ducommun.gameOfLife.Presets.ACORN
import io.ducommun.gameOfLife.Presets.R_PENTOMINO
import io.ducommun.gameOfLife.Presets.TEST_PATTERN
import io.ducommun.gameOfLife.viewModel.GameOfLifeViewModel
import io.ducommun.gameOfLife.viewModel.Scheduler
import org.khronos.webgl.Uint8ClampedArray
import org.khronos.webgl.set
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window

fun main(args: Array<String>) {

    val canvas = document.createElement("canvas") as HTMLCanvasElement
    canvas.width = 1024
    canvas.height = 1024

    document.body?.appendChild(canvas)

    val context = canvas.getContext("2d") as CanvasRenderingContext2D
    context.fillStyle = "#C1D5EC"
    context.fillRect(0.0, 0.0, 1024.0, 1024.0)

    GameOfLifeViewModel(
        scheduler = BrowserScheduler(),
        canvasWidth = 1024,
        canvasHeight = 1024,
        initialBoardWidth = 512,
        initialBoardHeight = 512,
        aliveColor = 0xff7A3433L.toInt(),
        deadColor = 0xffC1D5ECL.toInt(),
        initialFps = 60
    ).run {

        onDraw { pixels ->
            context.run {
                val imageData = createImageData(1024.0, 1024.0)
                val bytes = Uint8ClampedArray(1024 * 1024 * 4)
                pixels.forEachIndexed { index, pixel ->
                    bytes.asDynamic()[4 * index]     = pixel.and(0xff0000).shr(16)
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
        setPlane(R_PENTOMINO)
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