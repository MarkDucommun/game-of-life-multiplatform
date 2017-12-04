package io.ducommun.gameOfLife

class Plane(private val livingCells: Set<Coordinate> = emptySet()) : Set<Coordinate> by livingCells {

    fun next(): Plane = locationsToCheck.fold(emptySet) { nextLivingCells, coordinate ->

        val livingNeighbors = coordinate.neighbors.filter(this::alive).size

        if (livingNeighbors == 3 || coordinate.alive && livingNeighbors == 2) {
            nextLivingCells.plus(coordinate)
        } else {
            nextLivingCells
        }

    }.let(::Plane)

    fun toggleCell(location: Coordinate): Plane =
            (if (location.alive) livingCells.minus(location) else livingCells.plus(location)).let(::Plane)

    fun alive(location: Coordinate): Boolean = location.alive

    private val Coordinate.alive: Boolean get() = livingCells.contains(this)

    private val emptySet: Set<Coordinate> = kotlin.collections.emptySet()

    private val locationsToCheck: Set<Coordinate>
        get() = livingCells.flatMap { it.neighbors.plus(it) }.toSet()

    // OVERRIDES
    override fun equals(other: Any?): Boolean = other is Plane && livingCells == other.livingCells

    override fun hashCode(): Int = livingCells.hashCode()

    override fun toString(): String = livingCells.toString()
}