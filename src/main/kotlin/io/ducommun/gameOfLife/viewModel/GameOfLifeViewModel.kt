package io.ducommun.gameOfLife.viewModel

import io.ducommun.gameOfLife.Plane
import io.ducommun.gameOfLife.Coordinate
import io.ducommun.gameOfLife.HashSetPlane

interface Scheduler {
    fun immediately(task: () -> Unit)
    fun delay(milliseconds: Int, task: () -> Unit)
    fun onUIThread(task: () -> Unit)
}

data class Rect(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val color: Int
)

class GameOfLifeViewModel(
    private val scheduler: Scheduler,
    private val canvasWidth: Int,
    private val canvasHeight: Int,
    private val initialBoardWidth: Int,
    private val initialBoardHeight: Int,
    private val aliveColor: Int,
    private val deadColor: Int,
    private val initialFps: Int
) {

    private var draw: (IntArray) -> Unit = {}
    private var drawDiff: (List<Rect>) -> Unit = {}
    private var plane = HashSetPlane(setOf()) as Plane

    private val cellSize = canvasWidth / initialBoardWidth

    fun setPlane(plane: Plane) {

        this.plane = plane
        val planePixelArray = IntArray(canvasWidth * canvasHeight)

        for (x in 0 until initialBoardWidth) for (y in 0 until initialBoardHeight) {
            val alive = plane.alive(Coordinate(
                x = (x - initialBoardWidth / 2).toShort(),
                y = (-y + initialBoardHeight / 2 - 1).toShort())
            )

            val xCellOrigin = x * cellSize
            val yCellOrigin = y * cellSize

            for (cellX in xCellOrigin until xCellOrigin + cellSize)
                for (cellY in yCellOrigin until yCellOrigin + cellSize)
                    planePixelArray[cellX + canvasWidth * cellY] =
                        if (alive) aliveColor else deadColor
        }

        scheduler.onUIThread {
            draw(planePixelArray)
        }
    }

    fun start() {

        val rectsToChange =
            plane.nextDiff.revive
                .filterInBounds()
                .map { it.asRect(aliveColor) } +
            plane.nextDiff.kill
                .filterInBounds()
                .map { it.asRect(deadColor) }

        plane = plane.next()

        scheduler.delay(1000 / initialFps) {
            scheduler.onUIThread {
                drawDiff(rectsToChange)
            }
            start()
        }
    }

    private fun Iterable<Coordinate>.filterInBounds(): Iterable<Coordinate> = filter {
        it.x in -initialBoardWidth / 2 until initialBoardWidth / 2 &&
        it.y in -initialBoardHeight / 2 until initialBoardHeight / 2
    }

    private fun Coordinate.asRect(color: Int) = Rect(
        x = (x + initialBoardWidth / 2) * cellSize,
        y = (-y + initialBoardHeight / 2 - 1) * cellSize,
        width = cellSize,
        height = cellSize,
        color = color
    )

    fun onDraw(draw: (IntArray) -> Unit) {
        this.draw = draw
    }

    fun onDrawDiff(drawDiff: (List<Rect>) -> Unit) {
        this.drawDiff = drawDiff
    }
}