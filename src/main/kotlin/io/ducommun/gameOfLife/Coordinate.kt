package io.ducommun.gameOfLife

data class Coordinate(
        val x: Short,
        val y: Short
) {

    val neighbors: Set<Coordinate> get() = setOf(
            up,
            up.right,
            right,
            down.right,
            down,
            down.left,
            left,
            up.left
    )

    private val up: Coordinate get() = copy(y = (y + 1).toShort())
    private val right: Coordinate get() = copy(x = (x + 1).toShort())
    private val down: Coordinate get() = copy(y = (y - 1).toShort())
    private val left: Coordinate get() = copy(x = (x - 1).toShort())
}