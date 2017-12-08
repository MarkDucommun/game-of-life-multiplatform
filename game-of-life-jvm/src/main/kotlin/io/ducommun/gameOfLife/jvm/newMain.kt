package io.ducommun.gameOfLife.jvm

import io.ducommun.gameOfLife.Presets.ACORN
import io.ducommun.gameOfLife.Presets.BREEDER_ONE
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

    init {

        val self = this

        GameOfLifeViewModel(
            scheduler = CoroutineScheduler(),
            canvasWidth = 1024,
            canvasHeight = 1024,
            initialBoardWidth = 512,
            initialBoardHeight = 512,
            aliveColor = 0xff7A3433L.toInt(),
            deadColor = 0xffC1D5ECL.toInt(),
            initialFps = 120
        ).run {
            setPlane(BREEDER_ONE)
            onDraw(self::draw)
            onDrawDiff(self::drawDiff)
            start()
        }
    }

    override val root = vbox {
        imageview(image)
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

