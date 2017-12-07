import io.ducommun.gameOfLife.Coordinate
import io.ducommun.gameOfLife.HashSetPlane
import kotlin.js.Date
import kotlin.test.Ignore
import kotlin.test.Test

class TestSpeed {

    @Test
    @Ignore
    fun itIteratesThePlane() {

        val start = HashSetPlane(livingCells = setOf(
                Coordinate(x = 0, y = 0),
                Coordinate(x = 0, y = 1),
                Coordinate(x = 0, y = -1),
                Coordinate(x = -1, y = 0),
                Coordinate(x = 1, y = 1)
        ))

        val startTime = Date().getTime()

        (1..1000).fold(start) { plane, _ -> plane.next() }

        println("Elapsed - ${Date().getTime() - startTime}ms")
    }
}

