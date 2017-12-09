package io.ducommun.gameOfLife.viewModel

import io.ducommun.gameOfLife.Coordinate

class CoordinateTranslator(
    private val width: Int,
    private val height: Int,
    private val xTranslate: Int,
    private val yTranslate: Int
) {

    fun toBoard(canvas: Coordinate): Coordinate {
        return Coordinate(x = canvas.x - width / 2 + xTranslate, y = -canvas.y + height / 2 - 1 + yTranslate)
    }

    fun toCanvas(board: Coordinate): Coordinate {
        return Coordinate(x = board.x + width / 2 - xTranslate, y = -board.y + height / 2 - 1 + yTranslate)
    }
}