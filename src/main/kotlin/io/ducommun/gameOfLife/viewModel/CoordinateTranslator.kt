package io.ducommun.gameOfLife.viewModel

import io.ducommun.gameOfLife.Coordinate

class CoordinateTranslator(
    private val width: Int,
    private val height: Int,
    private val xTranslate: Int,
    private val yTranslate: Int
) {

    fun toBoard(canvas: Coordinate): Coordinate {
        if (width == 1) return Coordinate(x = canvas.x + xTranslate, y = canvas.y + yTranslate)

        return Coordinate(x = canvas.x - width / 2 + xTranslate, y = -canvas.y + height / 2 - 1 + yTranslate)
    }

    fun toCanvas(board: Coordinate): Coordinate {
        if (width == 1) return Coordinate(x = board.x + xTranslate, y = board.y + yTranslate)

        return Coordinate(x = board.x + width / 2 - xTranslate, y = -board.y + height / 2 - 1 + yTranslate)
    }
}