package io.ducommun.gameOfLife

interface Plane {

    fun next(): Plane

    val nextDiff: PlaneDiff

    fun toggleCell(location: Coordinate): Plane

    val size: Int

    fun alive(cell: Coordinate): Boolean

    fun toList(): List<Coordinate>
}

data class PlaneDiff(
    val revive: Set<Coordinate>,
    val kill: Set<Coordinate>
)

interface MutablePlane {

    fun next()

    val nextDiff: PlaneDiff

    val size: Int

    fun alive(cell: Coordinate): Boolean
}

