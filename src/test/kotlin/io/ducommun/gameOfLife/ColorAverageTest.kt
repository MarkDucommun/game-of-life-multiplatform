package io.ducommun.gameOfLife

import kotlin.test.Test
import kotlin.test.assertEquals

class ColorAverageTest {

    @Test
    fun averageOfTwoColors() {
        val one = 0x7A3433
        val two = 0xC1D5EC

        val actual = colorAverage(one, two).toString(16)
        val expected = 0x9D8490.toString(16)
        assertEquals(actual = actual, expected = expected)
    }

    @Test
    fun averageTwoSimpleNumbers() {
        val one = 0x7A
        val two = 0xC1

        assertEquals(expected = 0x9D.toString(16), actual = simpleAverage(one, two).toString(16))
    }

    @Test
    fun simpleShift() {
        assertEquals(actual = 0x7A0000.getFirst.toString(16), expected = 0x7A.toString(16))
        assertEquals(actual = 0x107A00.getSecond.toString(16), expected = 0x7A.toString(16))
        assertEquals(actual = 0x10007A.getThird.toString(16), expected = 0x7A.toString(16))
    }
}