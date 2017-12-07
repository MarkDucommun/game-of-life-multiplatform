package io.ducommun.gameOfLife

class HashSetPlane(private val livingCells: Set<Coordinate> = emptySet()) : Plane, Set<Coordinate> by livingCells {

    override val size: Int get() = livingCells.size

    override fun toList(): List<Coordinate> = livingCells.toList()

    override fun next(): HashSetPlane {

        val nextLivingCells = livingCells.toMutableSet()

        nextDiff.revive.forEach { coordinate -> nextLivingCells.add(coordinate) }
        nextDiff.kill.forEach { coordinate -> nextLivingCells.remove(coordinate) }

        return HashSetPlane(nextLivingCells)
    }

    override val nextDiff: PlaneDiff by lazy {

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

        PlaneDiff(revive = revive, kill = kill)
    }

    override fun toggleCell(location: Coordinate): HashSetPlane =
            (if (location.alive) livingCells.minus(location) else livingCells.plus(location)).let(::HashSetPlane)

    override fun alive(cell: Coordinate): Boolean = cell.alive

    private val Coordinate.alive: Boolean get() = livingCells.contains(this)

    private val emptySet: Set<Coordinate> = kotlin.collections.emptySet()

    private val locationsToCheck: Set<Coordinate>
        get() = livingCells.flatMap { it.neighbors.plus(it) }.toSet()

    // OVERRIDES
    override fun equals(other: Any?): Boolean = other is HashSetPlane && livingCells == other.livingCells

    override fun hashCode(): Int = livingCells.hashCode()

    override fun toString(): String = livingCells.toString()
}

