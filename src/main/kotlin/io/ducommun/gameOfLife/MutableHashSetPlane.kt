package io.ducommun.gameOfLife

class MutableHashSetPlane(livingCells: Set<Coordinate> = emptySet()): MutablePlane {

    private val livingCells = livingCells.toMutableSet()

    override val size: Int get() = livingCells.size

    override fun next() {

        nextDiff.revive.forEach { coordinate -> livingCells.add(coordinate) }
        nextDiff.kill.forEach { coordinate -> livingCells.remove(coordinate) }

        backingNextDiff = computeNextDiff()
    }

    private fun computeNextDiff(): PlaneDiff {

        val kill = mutableSetOf<Coordinate>()
        val revive = mutableSetOf<Coordinate>()

        locationsToCheck.forEach { coordinate ->

            val livingNeighbors = coordinate.neighbors.filter(this::alive).size
            val alive = coordinate.alive

            val shouldLive = livingNeighbors == 3 || alive && livingNeighbors == 2

            val shouldChange = alive != shouldLive

            if (shouldChange) {
                if (shouldLive) revive.add(coordinate) else kill.add(coordinate)
            }
        }

        return PlaneDiff(revive = revive, kill = kill)
    }

    private var backingNextDiff: PlaneDiff = computeNextDiff()

    override val nextDiff: PlaneDiff get() = backingNextDiff

    override fun alive(cell: Coordinate): Boolean = cell.alive

    private val Coordinate.alive: Boolean get() = livingCells.contains(this)

    private val locationsToCheck: Set<Coordinate>
        get() = livingCells.flatMap { it.neighbors.plus(it) }.toSet()

    // OVERRIDES
    override fun equals(other: Any?): Boolean = other is MutableHashSetPlane && livingCells == other.livingCells

    override fun hashCode(): Int = livingCells.hashCode()

    override fun toString(): String = livingCells.toString()
}