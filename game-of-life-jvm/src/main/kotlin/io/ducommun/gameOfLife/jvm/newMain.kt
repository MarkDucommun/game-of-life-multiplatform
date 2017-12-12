package io.ducommun.gameOfLife.jvm

import io.ducommun.gameOfLife.Presets.MAX
import io.ducommun.gameOfLife.viewModel.GameOfLifeViewModel
import io.ducommun.gameOfLife.viewModel.GameOfLifeViewModel.Stats
import io.ducommun.gameOfLife.viewModel.Rect
import javafx.scene.image.PixelFormat
import javafx.scene.image.WritableImage
import tornadofx.*
import kotlin.reflect.KClass

fun main(args: Array<String>) = launch<GameOfLifeWithViewModel>(args)

class GameOfLifeView : View() {

    private val image = WritableImage(1024, 1024)

    val game = GameOfLifeViewModel(
        scheduler = CoroutineScheduler(),
        canvasWidth = 1024,
        canvasHeight = 1024,
        initialBoardWidth = 512,
        initialBoardHeight = 512,
        aliveColor = 0xff7A3433L.toInt(),
        deadColor = 0xffC1D5ECL.toInt(),
        initialFps = 60
    ).apply {

        setPlane(MAX)

        onDraw(this@GameOfLifeView::draw)
        onDrawDiff(this@GameOfLifeView::drawDiff)
        onSetStats(this@GameOfLifeView::setTitle)

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
            toggleRunning()
        }
        shortcut("n") {
            next()
        }
        shortcut("s") {
            speedUp()
        }
        shortcut("shift+s") {
            slowDown()
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

    private fun setTitle(stats: Stats) {
        title = stats.run {
            val boardInfo = "$boardWidth x $boardHeight at (${origin.x},${origin.y})"
            val gameInfo = "$cellCount alive at generation $generation in $elapsedSeconds seconds running"

            if (running) {
                "$boardInfo, $gameInfo"
            } else {
                "$fps frames per second, $boardInfo, $gameInfo"
            }
        }
    }
}

class GameOfLifeWithViewModel : App() {
    override val primaryView: KClass<out UIComponent> = GameOfLifeView::class
}

