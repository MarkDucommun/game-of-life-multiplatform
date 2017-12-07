package io.ducommun.gameOfLife

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlaneTest {

    private val emptyPlane = HashSetPlane()

    @Test
    fun planesAreEqualIfTheyHaveTheSameSetOfCells() {

        assertTrue {

            val planeOne = HashSetPlane(livingCells = setOf(Coordinate(x = 0, y = 0)))

            val planeTwo = HashSetPlane(livingCells = setOf(Coordinate(x = 0, y = 0)))

            planeOne == planeTwo
        }
    }

    @Test
    fun nextStateOfAnEmptyPlaneIsAnEmptyPlane() {

        assertTrue {

            val nextPlane = emptyPlane.next()

            nextPlane == emptyPlane
        }
    }

    @Test
    fun nextStateOfAPlaneWithOneLiveCellIsAnEmptyPlane() {

        assertTrue {

            val nextPlane = HashSetPlane(livingCells = setOf(Coordinate(x = 0, y = 0))).next()

            nextPlane == emptyPlane
        }
    }

    @Test
    fun aliveCellsWithThreeNeighborsStayAlive() {

        assertTrue {

            val plane = HashSetPlane(livingCells = setOf(
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 0, y = 1),
                    Coordinate(x = 1, y = 1),
                    Coordinate(x = 1, y = 0)
            ))

            val nextPlane = plane.next()

            nextPlane == plane
        }
    }

    @Test
    fun deadCellsWithThreeNeighborsReviveAndAliveCellsWithTwoNeighborsStayAlive() {

        assertTrue {

            val plane = HashSetPlane(livingCells = setOf(
                    Coordinate(x = 0, y = -1),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 0, y = 1)
            ))

            val expectedNextPlane = HashSetPlane(livingCells = setOf(
                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 1, y = 0)
            ))

            val nextPlane = plane.next()

            nextPlane == expectedNextPlane
        }
    }

    @Test
    fun toggleCellOnDeadCellRevivesTheCell() {

        assertTrue {

            val coordinate = Coordinate(x = 0, y = 0)

            val plane = emptyPlane.toggleCell(location = coordinate)

            plane.alive(cell = coordinate)
        }
    }

    @Test
    fun toggleCellOnLivingCellKillsIt() {

        assertFalse {

            val coordinate = Coordinate(x = 0, y = 0)

            val plane = HashSetPlane(livingCells = setOf(coordinate)).toggleCell(location = coordinate)

            plane.alive(coordinate)
        }
    }
}