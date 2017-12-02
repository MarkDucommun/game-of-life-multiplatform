import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlaneTest {

    private val emptyPlane = Plane()

    @Test
    fun planesAreEqualIfTheyHaveTheSameSetOfCells() {

        assertTrue {

            val planeOne = Plane(livingCells = setOf(Coordinate(x = 0, y = 0)))

            val planeTwo = Plane(livingCells = setOf(Coordinate(x = 0, y = 0)))

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

            val nextPlane = Plane(livingCells = setOf(Coordinate(x = 0, y = 0))).next()

            nextPlane == emptyPlane
        }
    }

    @Test
    fun aliveCellsWithThreeNeighborsStayAlive() {

        assertTrue {

            val plane = Plane(livingCells = setOf(
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

            val plane = Plane(livingCells = setOf(
                    Coordinate(x = 0, y = -1),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 0, y = 1)
            ))

            val expectedNextPlane = Plane(livingCells = setOf(
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

            plane.alive(location = coordinate)
        }
    }

    @Test
    fun toggleCellOnLivingCellKillsIt() {

        assertFalse {

            val coordinate = Coordinate(x = 0, y = 0)

            val plane = Plane(livingCells = setOf(coordinate)).toggleCell(location = coordinate)

            plane.alive(coordinate)
        }
    }
}