package io.ducommun.gameOfLife.viewModel

import io.ducommun.gameOfLife.Coordinate
import kotlin.test.Test
import kotlin.test.assertEquals

class CoordinateTranslatorTest {

    private lateinit var subject: CoordinateTranslator

    @Test
    fun it_translates_coordinates() {

        subject = CoordinateTranslator(
            height = 4,
            width = 4,
            xTranslate = 0,
            yTranslate = 0
        )

        Coordinate(x = -1, y = 1) onBoardTranslatesTo Coordinate(x = 1, y = 0)
        Coordinate(x = 0, y = 1) onBoardTranslatesTo Coordinate(x = 2, y = 0)
        Coordinate(x = 1, y = 1) onBoardTranslatesTo Coordinate(x = 3, y = 0)

        Coordinate(x = -1, y = 0) onBoardTranslatesTo Coordinate(x = 1, y = 1)
        Coordinate(x = 0, y = 0) onBoardTranslatesTo Coordinate(x = 2, y = 1)
        Coordinate(x = 1, y = 0) onBoardTranslatesTo Coordinate(x = 3, y = 1)

        Coordinate(x = -1, y = -1) onBoardTranslatesTo Coordinate(x = 1, y = 2)
        Coordinate(x = 0, y = -1) onBoardTranslatesTo Coordinate(x = 2, y = 2)
        Coordinate(x = 1, y = -1) onBoardTranslatesTo Coordinate(x = 3, y = 2)
    }

    @Test
    fun it_translates_coordinates_with_an_x_shift() {
        subject = CoordinateTranslator(
            height = 4,
            width = 4,
            xTranslate = 1,
            yTranslate = 0
        )

        Coordinate(x = 0, y = 1) onBoardTranslatesTo Coordinate(x = 1, y = 0)
        Coordinate(x = 1, y = 1) onBoardTranslatesTo Coordinate(x = 2, y = 0)
        Coordinate(x = 2, y = 1) onBoardTranslatesTo Coordinate(x = 3, y = 0)

        Coordinate(x = 0, y = 0) onBoardTranslatesTo Coordinate(x = 1, y = 1)
        Coordinate(x = 1, y = 0) onBoardTranslatesTo Coordinate(x = 2, y = 1)
        Coordinate(x = 2, y = 0) onBoardTranslatesTo Coordinate(x = 3, y = 1)

        Coordinate(x = 0, y = -1) onBoardTranslatesTo Coordinate(x = 1, y = 2)
        Coordinate(x = 1, y = -1) onBoardTranslatesTo Coordinate(x = 2, y = 2)
        Coordinate(x = 2, y = -1) onBoardTranslatesTo Coordinate(x = 3, y = 2)
    }


    @Test
    fun it_translates_coordinates_with_an_y_shift() {
        subject = CoordinateTranslator(
            height = 4,
            width = 4,
            xTranslate = 0,
            yTranslate = 1
        )

        Coordinate(x = -1, y = 1) onBoardTranslatesTo Coordinate(x = 1, y = 1)
        Coordinate(x = 0, y = 1) onBoardTranslatesTo Coordinate(x = 2, y = 1)
        Coordinate(x = 1, y = 1) onBoardTranslatesTo Coordinate(x = 3, y = 1)

        Coordinate(x = -1, y = 0) onBoardTranslatesTo Coordinate(x = 1, y = 2)
        Coordinate(x = 0, y = 0) onBoardTranslatesTo Coordinate(x = 2, y = 2)
        Coordinate(x = 1, y = 0) onBoardTranslatesTo Coordinate(x = 3, y = 2)

        Coordinate(x = -1, y = -1) onBoardTranslatesTo Coordinate(x = 1, y = 3)
        Coordinate(x = 0, y = -1) onBoardTranslatesTo Coordinate(x = 2, y = 3)
        Coordinate(x = 1, y = -1) onBoardTranslatesTo Coordinate(x = 3, y = 3)
    }

    private infix fun Coordinate.onBoardTranslatesTo(canvas: Coordinate) {
        assertEquals(expected = canvas, actual = subject.toCanvas(this))
        assertEquals(expected = this, actual = subject.toBoard(canvas))
    }
}