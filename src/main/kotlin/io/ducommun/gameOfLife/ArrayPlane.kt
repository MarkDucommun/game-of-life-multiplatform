package io.ducommun.gameOfLife

class ArrayPlane private constructor(
        private val plane: BooleanArray
) : Plane {
    private val allCoordinates: Sequence<Coordinate>
        get() =
            (-dimension / 2 until dimension / 2).asSequence()
                    .flatMap { x ->
                        (-dimension / 2 until dimension / 2).asSequence()
                                .map { y ->
                                    Coordinate(x = x.toShort(), y = y.toShort())
                                }
                    }

    private val livingCells: Sequence<Coordinate>
        get() = allCoordinates.filter { plane[it] }

    override val size: Int
        get() = plane.count { it }

    override fun toList(): List<Coordinate> = livingCells.toList()

    override fun next(): Plane {

        val nextPlane = BooleanArray(dimension * dimension)

        allCoordinates.forEach { coordinate ->
            val living = plane[coordinate]
            val livingNeighbors = coordinate.neighbors
                    .filter { it.x >= -dimension / 2 && it.x < dimension / 2 && it.y >= -dimension / 2 && it.y < dimension / 2 }
                    .filter { plane[it] }.count()
            if (livingNeighbors == 3 || living && livingNeighbors == 2) nextPlane[coordinate] = true
        }

        return ArrayPlane(nextPlane)
    }

    override val nextDiff: PlaneDiff by lazy {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toggleCell(location: Coordinate): Plane {
        TODO()
    }

    override fun alive(cell: Coordinate): Boolean = cell.x >= -dimension / 2 && cell.x < dimension / 2 && cell.y >= -dimension / 2 && cell.y < dimension / 2 && plane[cell]

    companion object {

        fun create(livingCells: Set<Coordinate> = emptySet()): Plane {

            val plane = BooleanArray(300 * 300)

            livingCells.forEach { coordinate ->
                plane[coordinate] = true
            }

            return ArrayPlane(plane)
        }

        private val dimension = 300

        operator private fun BooleanArray.set(coordinate: Coordinate, value: Boolean) {
            this[indexOf(coordinate)] = value
        }

        operator private fun BooleanArray.get(coordinate: Coordinate): Boolean {
            return this[indexOf(coordinate)]
        }

        private fun indexOf(coordinate: Coordinate): Int {
            return (coordinate.x + dimension / 2) * dimension + (coordinate.y + dimension / 2)
        }
    }
}