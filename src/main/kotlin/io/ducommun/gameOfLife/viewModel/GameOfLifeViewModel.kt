package io.ducommun.gameOfLife.viewModel

import io.ducommun.gameOfLife.*
import io.ducommun.gameOfLife.Presets.EMPTY

class GameOfLifeViewModel internal constructor(
    private val scheduler: Scheduler,
    initialBoardWidth: Int,
    initialBoardHeight: Int,
    private var state: ViewState,
    private val actions: Actions
) : ViewState by state, Actions by actions {

    data class Settings(
        val plane: Plane = EMPTY,
        val canvas: Dimensions,
        val board: Dimensions,
        val colors: Colors,
        val framesPerSecond: Int = 60
    )

    data class Colors(
        val alive: Int,
        val dead: Int,
        val gradient: Boolean = false
    )

    data class Dimensions(
        val width: Int,
        val height: Int
    )

    companion object {

        fun create(
            settings: Settings,
            actions: Actions,
            scheduler: Scheduler
        ): GameOfLifeViewModel {

            return GameOfLifeViewModel(
                scheduler = scheduler,
                initialBoardHeight = settings.board.height,
                initialBoardWidth = settings.board.width,
                state = MutableViewState(
                    fps = settings.framesPerSecond,
                    canvas = settings.canvas,
                    board = settings.board,
                    aliveColor = settings.colors.alive,
                    deadColor = settings.colors.dead,
                    useGradient = settings.colors.gradient
                ),
                actions = actions
            ).apply {

                setPlane(settings.plane)
            }
        }
    }

    private var boardWidth = initialBoardWidth
    private var boardHeight = initialBoardHeight

    private var xTranslation = 0
    private var yTranslation = 0

    private var plane = HashSetPlane(setOf()) as Plane

    private var drawOnNextIteration = false
    private var running = false
    private val cellSize get() = canvas.width / boardWidth

    private var gradient = emptyList<Int>()

    private val translator
        get() = CoordinateTranslator(
            width = boardWidth,
            height = boardHeight,
            xTranslate = xTranslation,
            yTranslate = yTranslation
        )

    fun setPlane(plane: Plane) {

        this.plane = plane

        updateStats()

        if (boardWidth > canvas.width) recalculateGradient()

        render()
    }

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

        generation++

        updateStats()
    }

    fun toggleRunning() {
        if (running) stop() else start()
    }

    fun start() {

        if (running) return

        running = true
        startTime = currentTime()

        loop()
    }

    fun stop() {
        elapsedTime += currentTime() - startTime
        running = false
    }

    fun zoomIn() {
        if (boardWidth <= 1) return

        boardWidth /= 2
        boardHeight /= 2

        if (boardWidth > canvas.width) recalculateGradient()
        resetCanvas()
    }

    fun zoomOut() {
        boardWidth *= 2
        boardHeight *= 2

        if (boardWidth > canvas.width) recalculateGradient()
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

                if (drawOnNextIteration) {

                    drawOnNextIteration = false
                    render()

                } else {

                    renderDiff(diff)
                }

                generation++
                updateStats()

                loop()

            } else {
                plane = lastPlane
            }
        }
    }

    private fun render() {

        val planePixelArray = IntArray(canvas.width * canvas.height)

        if (boardWidth > canvas.width) {

            planePixelArray.renderBoardLargerThanCanvas()

        } else {

            planePixelArray.renderBoardSmallerThanCanvas()
        }

        scheduler.onUIThread {
            drawPlane(planePixelArray)
        }
    }

    private fun renderDiff(diff: PlaneDiff) {
        val rectsToChange = if (boardWidth > canvas.width) {
            diff.rectsToChangeBoardLargerThanCanvas()
        } else {
            diff.rectsToChangeBoardSmallerThanCanvas()
        }

        scheduler.onUIThread { drawPlaneDiff(rectsToChange) }
    }

    private fun PlaneDiff.rectsToChangeBoardSmallerThanCanvas(): List<Rectangle> =
        revive
            .filterInBounds()
            .map { it.asRect(aliveColor) } +
        kill
            .filterInBounds()
            .map { it.asRect(deadColor) }

    private fun PlaneDiff.rectsToChangeBoardLargerThanCanvas(): List<Rectangle> {

        val widthRatio = boardWidth / canvas.width
        val heightRatio = boardHeight / canvas.height

        val changedBoardCoordinates = (revive + kill).filter { coordinate ->
            coordinate.x in xTranslation - boardWidth / 2 until xTranslation + boardWidth / 2 &&
            coordinate.y in yTranslation - boardHeight / 2 until yTranslation + boardHeight / 2
        }

        val changedCanvasCoordinates = mutableSetOf<Coordinate>()

        changedBoardCoordinates.forEach { boardCoordinate ->

            val translatedBoardCoordinate = translator.toCanvas(boardCoordinate)

            val canvasCoordinate = Coordinate(
                x = translatedBoardCoordinate.x / widthRatio,
                y = translatedBoardCoordinate.y / heightRatio
            )

            changedCanvasCoordinates.add(canvasCoordinate)
        }

        return changedCanvasCoordinates.map { canvasCoordinate ->

            val boardCoordinate = translator.toBoard(
                Coordinate(
                    x = canvasCoordinate.x * widthRatio,
                    y = canvasCoordinate.y * heightRatio
                )
            )

            val xRange = boardCoordinate.x until boardCoordinate.x + widthRatio
            val yRange = boardCoordinate.y downTo boardCoordinate.y - heightRatio + 1

            var aliveCount = 0

            for (boardX in xRange) for (boardY in yRange) {
                if (plane.alive(Coordinate(x = boardX, y = boardY))) aliveCount += 1
            }

            Rectangle(
                x = canvasCoordinate.x.toInt(),
                y = canvasCoordinate.y.toInt(),
                width = 1,
                height = 1,
                color = when {
                    useGradient -> gradient[aliveCount]
                    aliveCount > 0 -> aliveColor
                    else -> deadColor
                }
            )
        }
    }

    private fun IntArray.renderBoardLargerThanCanvas() {

        for (x in 0 until canvas.width) for (y in 0 until canvas.height) {

            val boardCoordinate = translator.toBoard(
                Coordinate(
                    x = x * boardWidth / canvas.width,
                    y = y * boardHeight / canvas.height
                )
            )

            val xRange = boardCoordinate.x until boardCoordinate.x + boardWidth / canvas.width
            val yRange = boardCoordinate.y downTo boardCoordinate.y - boardHeight / canvas.height + 1

            var aliveCount = 0

            for (boardX in xRange) for (boardY in yRange) {
                if (plane.alive(Coordinate(x = boardX, y = boardY))) aliveCount += 1
            }

            if (useGradient) {
                this[x + y * canvas.width] = gradient[aliveCount]
            } else {
                if (aliveCount > 0) {
                    this[x + y * canvas.width] = aliveColor
                } else {
                    this[x + y * canvas.width] = deadColor
                }
            }
        }
    }

    private fun IntArray.renderBoardSmallerThanCanvas() {
        for (x in 0 until boardWidth) for (y in 0 until boardHeight)
            renderCell(coordinate = Coordinate(x = x, y = y))
    }

    private fun IntArray.renderCell(coordinate: Coordinate) {
        val alive = plane.alive(translator.toBoard(coordinate))

        val xCellMin = coordinate.x * cellSize
        val xCellMax = xCellMin + cellSize

        val yCellMin = coordinate.y * cellSize
        val yCellMax = yCellMin + cellSize

        val xRange = xCellMin until xCellMax
        val yRange = yCellMin until yCellMax

        for (boardX in xRange) for (boardY in yRange) {
            this[boardX + canvas.width * boardY] = if (alive) aliveColor else deadColor
        }
    }

    data class Stats(
        val running: Boolean,
        val fps: Double,
        val origin: Coordinate,
        val xDimension: Int,
        val yDimension: Int,
        val generation: Int,
        val cellCount: Int,
        val elapsedSeconds: Long = 0
    )

    private var previousGeneration = 0
    private var generation = 0

    private var elapsedTime = 0L
    private var startTime = 0L
    private var lastSetTitle = 0L

    private fun updateStats() {

        val now = currentTime()

        updateFps(now)
        scheduler.onUIThread { updateStats(currentStats(now)) }
        lastSetTitle = now
    }

    private fun currentStats(now: Long) = Stats(
        running = running,
        fps = actualFps,
        origin = Coordinate(
            x = xTranslation,
            y = yTranslation
        ),
        xDimension = boardWidth,
        yDimension = boardHeight,
        generation = generation,
        cellCount = plane.size,
        elapsedSeconds = now - startTime + elapsedTime
    )

    private var lastSetFps = 0L
    private var actualFps = 0.0

    private fun updateFps(now: Long) {
        if (now - lastSetFps >= 1000) {
            actualFps = (generation - previousGeneration) / (now - lastSetFps).toDouble() * 1000
            lastSetFps = now
            previousGeneration = generation
        }
    }

    private fun recalculateGradient() {
        val cellDimension = boardWidth / canvas.width
        val numberOfColors = cellDimension * cellDimension

        gradient = (0..numberOfColors).map { aliveCount ->
            val deadCount = numberOfColors - aliveCount

            0xff000000 +
            ((aliveColor.and(0xff0000).shr(16) * aliveCount + deadColor.and(0xff0000).shr(16) * deadCount) / numberOfColors) * 0x10000 +
            ((aliveColor.and(0xff00).shr(8) * aliveCount + deadColor.and(0xff00).shr(8) * deadCount) / numberOfColors) * 0x100 +
            ((aliveColor.and(0xff) * aliveCount + deadColor.and(0xff) * deadCount) / numberOfColors)

        }.map(Long::toInt)
    }

    private fun Iterable<Coordinate>.filterInBounds(): Iterable<Coordinate> = filter {
        val canvasCoordinates = translator.toCanvas(it)
        canvasCoordinates.x * (canvas.width / boardWidth) in 0 until canvas.width &&
        canvasCoordinates.y * (canvas.height / boardHeight) in 0 until canvas.height
    }

    private fun Coordinate.asRect(color: Int): Rectangle {

        val canvasCoordinates = translator.toCanvas(this)
        return Rectangle(
            x = canvasCoordinates.x * cellSize,
            y = canvasCoordinates.y * cellSize,
            width = cellSize,
            height = cellSize,
            color = color
        )
    }

    private fun resetCanvas() {
        if (running) drawOnNextIteration = true else render()
    }
}