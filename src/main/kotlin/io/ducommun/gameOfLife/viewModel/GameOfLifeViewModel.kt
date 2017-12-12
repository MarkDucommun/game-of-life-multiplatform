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
    initialFps: Int
) {

    private var fps = initialFps

    private var boardWidth = initialBoardWidth
    private var boardHeight = initialBoardHeight

    private var xTranslation = 0
    private var yTranslation = 0

    private var draw: (IntArray) -> Unit = {}
    private var drawDiff: (List<Rect>) -> Unit = {}
    private var setStats: (Stats) -> Unit = {}
    private var plane = HashSetPlane(setOf()) as Plane

    data class Stats(
        val running: Boolean,
        val fps: Int,
        val origin: Coordinate,
        val xDimension: Int,
        val yDimension: Int,
        val generation: Int,
        val cellCount: Int,
        val elapsedSeconds: Int
    )

    private var drawOnNextIteration = false
    private var running = false
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

    fun onDraw(draw: (IntArray) -> Unit) {
        this.draw = draw
    }

    fun onDrawDiff(drawDiff: (List<Rect>) -> Unit) {
        this.drawDiff = drawDiff
    }

    fun onSetStats(setStats: (Stats) -> Unit) { this.setStats = setStats }

    fun toggle(canvasX: Double, canvasY: Double) {

        val scaledCanvas = Coordinate(x = (canvasX / cellSize).toInt(), y = (canvasY / cellSize).toInt())

        val board = translator.toBoard(scaledCanvas)

        plane = plane.toggleCell(board)

        resetCanvas()
    }

    fun next() {
        if (running) return

        renderDiff(plane.nextDiff)

        plane = plane.next()
    }

    fun toggleRunning() { if (running) stop() else start() }

    fun start() {

        if (running) return

        running = true

        loop()
    }

    fun stop() {
        running = false
    }

    fun zoomIn() {
        if (boardWidth > 1) {
            boardWidth /= 2
            boardHeight /= 2
            resetCanvas()
        }
    }

    fun zoomOut() {
        boardWidth *= 2
        boardHeight *= 2
        resetCanvas()
    }

    fun panLeft() {
        xTranslation -= 1
        drawOnNextIteration = true
        resetCanvas()
    }

    fun panRight() {
        xTranslation += 1
        drawOnNextIteration = true
        resetCanvas()
    }

    fun panUp() {
        yTranslation += 1
        drawOnNextIteration = true
        resetCanvas()
    }

    fun panDown() {
        yTranslation -= 1
        drawOnNextIteration = true
        resetCanvas()
    }

    fun slowDown() {
        if (fps / 2 < 1) fps = 1 else fps /= 2
    }

    fun speedUp() {
        if (fps * 2 > 125) fps = 125 else fps *= 2
    }

    private fun loop() {

        val diff = plane.nextDiff

        val lastPlane = plane

        scheduler.immediately {
            plane = plane.next()
        }

        scheduler.delay(1000 / fps) {

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

    private fun renderDiff(diff: PlaneDiff) {
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

    private fun resetCanvas() {
        if (running) drawOnNextIteration = true else render()
    }
}