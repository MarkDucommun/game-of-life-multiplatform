package io.ducommun.gameOfLife.jvm

import io.ducommun.gameOfLife.Colors
import io.ducommun.gameOfLife.Presets.NOAHS_ARK
import io.ducommun.gameOfLife.viewModel.GameOfLifeViewModel
import io.ducommun.gameOfLife.viewModel.GameOfLifeViewModel.Stats
import io.ducommun.gameOfLife.viewModel.Rectangle
import javafx.scene.image.PixelFormat
import javafx.scene.image.WritableImage
import tornadofx.*
import kotlin.math.floor
import kotlin.reflect.KClass

fun main(args: Array<String>) = launch<GameOfLifeWithViewModel>(args)

val boardDimension = 2048
val canvasDimension = 1024
val plane = NOAHS_ARK
val aliveColor = Colors.LIGHT_YELLOW
val deadColor = Colors.FLAT_BLACK

class GameOfLifeView : View() {

    private val image = WritableImage(canvasDimension, canvasDimension)

    private val game = GameOfLifeViewModel.create(
        settings = GameOfLifeViewModel.Settings(
            plane = plane,
            canvas = GameOfLifeViewModel.Dimensions(
                width = canvasDimension,
                height = canvasDimension
            ),
            board = GameOfLifeViewModel.Dimensions(
                width = boardDimension,
                height = boardDimension
            ),
            colors = GameOfLifeViewModel.Colors(
                alive = 0xFF_00_00_00.toInt() + aliveColor,
                dead =  0xFF_00_00_00.toInt() + deadColor,
                gradient = true
            )
        ),
        actions = GameOfLifeViewModel.Actions(
            drawPlane = this@GameOfLifeView::draw,
            drawPlaneDiff = this@GameOfLifeView::drawDiff,
            updateStats = this@GameOfLifeView::setTitle,
            currentTime = { System.currentTimeMillis() }
        ),
        scheduler = CoroutineScheduler
    ).apply {
        shortcut("left") { panLeft() }
        shortcut("right") { panRight() }
        shortcut("up") { panUp() }
        shortcut("down") { panDown() }
        shortcut("equals") { zoomIn() }
        shortcut("minus") { zoomOut() }
        shortcut("space") { toggleRunning() }
        shortcut("n") { next() }
        shortcut("s") { speedUp() }
        shortcut("shift+s") { slowDown() }
        shortcut("r") { setPlane(plane) }
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
            canvasDimension, canvasDimension,
            PixelFormat.getIntArgbInstance(),
            canvas,
            0, canvasDimension
        )
    }

    private fun drawDiff(rectangles: List<Rectangle>) {
        rectangles.forEach { rect ->
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
            val boardInfo = "$xDimension x $yDimension at (${origin.x},${origin.y})"
            val gameInfo = "$cellCount alive cells at generation $generation after ${elapsedSeconds / 1000} seconds running"
            val base = "$boardInfo :: $gameInfo"

            if (running) "${floor(fps * 100) / 100} frames per second :: $base" else base
        }
    }
}

class GameOfLifeWithViewModel : App() {
    override val primaryView: KClass<out UIComponent> = GameOfLifeView::class
}

