package io.ducommun.gameOfLife.viewModel

import io.ducommun.gameOfLife.Coordinate
import io.ducommun.gameOfLife.HashSetPlane
import io.ducommun.gameOfLife.Plane
import io.ducommun.gameOfLife.PlaneDiff

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
    initialBoardWidth: Int,
    initialBoardHeight: Int,
    private val aliveColor: Int,
    private val deadColor: Int,
    private val initialFps: Int
) {

    private var boardWidth = initialBoardWidth
    private var boardHeight = initialBoardHeight

    private var xTranslation = 0
    private var yTranslation = 0

    private var draw: (IntArray) -> Unit = {}
    private var drawDiff: (List<Rect>) -> Unit = {}
    private var plane = HashSetPlane(setOf()) as Plane

    private var drawOnNextIteration = false
    private var running = false

    val isRunning get() = running

    private val cellSize get() = canvasWidth / boardWidth

    private val translator
        get() = CoordinateTranslator(
            width = boardWidth,
            height = boardHeight,
            xTranslate = xTranslation,
            yTranslate = yTranslation
        )

    fun setPlane(plane: Plane) {

        this.plane = plane

        render()
    }

    fun toggle(canvasX: Double, canvasY: Double) {

        val scaledCanvas = Coordinate(x = (canvasX / cellSize).toInt(), y = (canvasY / cellSize).toInt())

        val board = translator.toBoard(scaledCanvas)

        plane = plane.toggleCell(board)

        if (running) {

            drawOnNextIteration = true

        } else {

            render()
        }

    }

    fun next() {
        if (running) return

        renderDiff(plane.nextDiff)

        plane = plane.next()
    }

    fun start() {

        running = true

        loop()
    }

    fun stop() {
        running = false
    }

    fun loop() {

        val diff = plane.nextDiff

        val lastPlane = plane

        scheduler.immediately {
            plane = plane.next()
        }

        scheduler.delay(1000 / initialFps) {

            if (running) {

                if (drawOnNextIteration || boardWidth > canvasWidth) {

                    drawOnNextIteration = false
                    render()

                } else {

                    renderDiff(diff)
                }

                loop()

            } else {
                plane = lastPlane
            }
        }
    }

    fun renderDiff(diff: PlaneDiff) {
        val rectsToChange =
                diff.revive
                        .filterInBounds()
                        .map { it.asRect(aliveColor) } +
                        diff.kill
                                .filterInBounds()
                                .map { it.asRect(deadColor) }

        scheduler.onUIThread {
            drawDiff(rectsToChange)
        }
    }

    private fun render() {
        val planePixelArray = IntArray(canvasWidth * canvasHeight)

        if (boardWidth > canvasWidth) {

            for (x in 0 until canvasWidth) for (y in 0 until canvasHeight) {
                val boardCoordinate = translator
                    .toBoard(Coordinate(
                        x = x * boardWidth / canvasWidth,
                        y = y * boardHeight / canvasHeight))

                var count = 0

                for (boardX in boardCoordinate.x until boardCoordinate.x + boardWidth / canvasWidth)
                    for (boardY in boardCoordinate.y downTo boardCoordinate.y - boardHeight / canvasHeight + 1) {
                        if (plane.alive(Coordinate(x = boardX, y = boardY))) {
                            count += 1
                        }
                    }

                if (count > 0) {
                    planePixelArray[x + y * canvasWidth] = aliveColor
                } else {
                    planePixelArray[x + y * canvasWidth] = deadColor
                }
            }


        } else {
            for (x in 0 until boardWidth) for (y in 0 until boardHeight) {
                val alive = plane.alive(translator.toBoard(Coordinate(x, y)))

                val xCellOrigin = x * cellSize
                val yCellOrigin = y * cellSize

                for (cellX in xCellOrigin until xCellOrigin + cellSize)
                    for (cellY in yCellOrigin until yCellOrigin + cellSize)
                        planePixelArray[cellX + canvasWidth * cellY] =
                            if (alive) aliveColor else deadColor
            }
        }

        scheduler.onUIThread {
            draw(planePixelArray)
        }
    }

    private fun Iterable<Coordinate>.filterInBounds(): Iterable<Coordinate> = filter {
        val canvas = translator.toCanvas(it)
        canvas.x * (canvasWidth / boardWidth) in 0 until canvasWidth &&
        canvas.y * (canvasHeight / boardHeight) in 0 until canvasHeight
    }

    private fun Coordinate.asRect(color: Int): Rect {

        val canvas = translator.toCanvas(this)
        return Rect(
            x = canvas.x * cellSize,
            y = canvas.y * cellSize,
            width = cellSize,
            height = cellSize,
            color = color
        )
    }

    fun onDraw(draw: (IntArray) -> Unit) {
        this.draw = draw
    }

    fun onDrawDiff(drawDiff: (List<Rect>) -> Unit) {
        this.drawDiff = drawDiff
    }

    fun zoomIn() {
        if (boardWidth > 1) {
            boardWidth /= 2
            boardHeight /= 2
            if (!running) render() else drawOnNextIteration = true
        }
    }

    fun zoomOut() {
        boardWidth *= 2
        boardHeight *= 2
        if (!running) render() else drawOnNextIteration = true
    }

    fun panLeft() {
        xTranslation -= 1
        drawOnNextIteration = true
        if (!running) render() else drawOnNextIteration = true
    }

    fun panRight() {
        xTranslation += 1
        drawOnNextIteration = true
        if (!running) render() else drawOnNextIteration = true
    }

    fun panUp() {
        yTranslation += 1
        drawOnNextIteration = true
        if (!running) render() else drawOnNextIteration = true
    }

    fun panDown() {
        yTranslation -= 1
        drawOnNextIteration = true
        if (!running) render() else drawOnNextIteration = true
    }
}