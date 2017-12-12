package io.ducommun.gameOfLife.jvm

import io.ducommun.gameOfLife.Presets.MAX
import io.ducommun.gameOfLife.viewModel.GameOfLifeViewModel
import io.ducommun.gameOfLife.viewModel.Rect
import javafx.scene.image.PixelFormat
import javafx.scene.image.WritableImage
import tornadofx.*
import kotlin.reflect.KClass

fun main(args: Array<String>) = launch<GameOfLifeWithViewModel>(args)

class GameOfLifeView : View() {

    private val image = WritableImage(1024, 1024)

    private val self = this

    val game = GameOfLifeViewModel(
        scheduler = CoroutineScheduler(),
        canvasWidth = 1024,
        canvasHeight = 1024,
        initialBoardWidth = 2048,
        initialBoardHeight = 2048,
        aliveColor = 0xff7A3433L.toInt(),
        deadColor = 0xffC1D5ECL.toInt(),
        initialFps = 60
    ).apply {

        setPlane(MAX)

        onDraw(self::draw)
        onDrawDiff(self::drawDiff)

        shortcut("left") {
            panLeft()
        }
        shortcut("right") {
            panRight()
        }
        shortcut("up"){
            panUp()
        }
        shortcut("down") {
            panDown()
        }
        shortcut("equals") {
            zoomIn()
        }
        shortcut("minus") {
            zoomOut()
        }
        shortcut("space") {
            if (isRunning) stop() else start()
        }
        shortcut("n") {
            next()
        }
    }

    override val root = vbox {
        imageview(image).run {
            setOnMouseClicked { event ->
                game.toggle(canvasX = event.x, canvasY = event.y)
            }
        }
    }

    private fun draw(canvas: IntArray) {
        image.pixelWriter.setPixels(
            0, 0,
            1024, 1024,
            PixelFormat.getIntArgbInstance(),
            canvas,
            0, 1024)
    }

    private fun drawDiff(rects: List<Rect>) {
        rects.forEach { rect ->
            image.pixelWriter.setPixels(
                rect.x, rect.y,
                rect.width, rect.height,
                PixelFormat.getIntArgbInstance(),
                IntArray(rect.width * rect.height) { rect.color },
                0, rect.width
            )
        }
    }
}

class GameOfLifeWithViewModel : App() {
    override val primaryView: KClass<out UIComponent> = GameOfLifeView::class
}

