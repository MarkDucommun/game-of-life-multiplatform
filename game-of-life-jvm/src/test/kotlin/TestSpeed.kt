import io.ducommun.gameOfLife.MAX
import io.ducommun.gameOfLife.MUTABLE_MAX
import org.junit.Test

class TestSpeed {

    @Test
    fun `it iterates the plane`() {

        val start = MAX

        val startTime = System.currentTimeMillis()

        (1..630).fold(start) { plane, _ -> plane.next() }

        println("Immutable: Elapsed - ${System.currentTimeMillis() - startTime}ms")
    }

    @Test
    fun `it iterates the mutable plane`() {

        val plane = MUTABLE_MAX

        val startTime = System.currentTimeMillis()

        repeat(630) { plane.next() }

        println("Mutable: Elapsed - ${System.currentTimeMillis() - startTime}ms")
    }
}