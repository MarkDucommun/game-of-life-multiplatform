import io.ducommun.gameOfLife.Coordinate
import io.ducommun.gameOfLife.Plane
import org.junit.Ignore
import org.junit.Test

class TestSpeed {

    @Test
    @Ignore
    fun `it iterates the plane`() {

        // r-pentamino
        val start = Plane(livingCells = setOf(
                Coordinate(x = 0, y = 0),
                Coordinate(x = 0, y = 1),
                Coordinate(x = 0, y = -1),
                Coordinate(x = -1, y = 0),
                Coordinate(x = 1, y = 1)
        ))

        val startTime = System.currentTimeMillis()

        (1..1000).fold(start) { plane, _ -> plane.next() }

        println("Elapsed - ${System.currentTimeMillis() - startTime}ms")
    }
}