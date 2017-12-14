package io.ducommun.gameOfLife

fun colorAverage(one: Int, two: Int): Int {

    return (one.getFirst + two.getFirst) / 2 * 0x10000 +
    (one.getSecond + two.getSecond) / 2 * 0x100 +
    0x90
}

fun simpleAverage(one: Int, two: Int): Int {
    return (one + two) / 2
}

val Int.getFirst: Int get() = and(0xff0000).shr(16)
val Int.getSecond: Int get() = and(0xff00).shr(8)
val Int.getThird: Int get() = and(0xff)