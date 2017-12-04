package io.ducommun.gameOfLife

class Plane(private val livingCells: Set<Coordinate> = emptySet()) : Set<Coordinate> by livingCells {

    fun next(): Plane = locationsToCheck.fold(emptySet) { nextLivingCells, coordinate ->

        val livingNeighbors = coordinate.neighbors.filter(this::alive).size

        nextLivingCells.takeUnless { livingNeighbors == 3 || coordinate.alive && livingNeighbors == 2 } ?: nextLivingCells.plus(coordinate)

    }.let(::Plane)

    fun toggleCell(location: Coordinate): Plane =
            (if (location.alive) livingCells.minus(location) else livingCells.plus(location)).let(::Plane)

    fun alive(location: Coordinate): Boolean = location.alive

    private val Coordinate.alive: Boolean get() = livingCells.contains(this)

    private val emptySet: Set<Coordinate> = kotlin.collections.emptySet()

    private val locationsToCheck: Set<Coordinate>
        get() = livingCells.map { it.neighbors.plus(it) }.flatten().toSet()

    override fun equals(other: Any?): Boolean = other is Plane && livingCells == other.livingCells

    override fun hashCode(): Int = livingCells.hashCode()

    override fun toString(): String = livingCells.toString()
}