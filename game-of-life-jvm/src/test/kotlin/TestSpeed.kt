import io.ducommun.gameOfLife.*
import org.junit.Test

class TestSpeed {

    @Test
    fun `it iterates the plane`() {

        val start = Presets.MAX

        val startTime = System.currentTimeMillis()

        (1..630).fold(start) { plane, _ -> plane.next() }

        println("Immutable: Elapsed - ${System.currentTimeMillis() - startTime}ms")
    }

    @Test
    fun `it iterates the mutable plane`() {

        val plane = ArrayPlane.create(Presets.MAX as HashSetPlane)

        val startTime = System.currentTimeMillis()

        repeat(630) { plane.next() }

        println("Mutable: Elapsed - ${System.currentTimeMillis() - startTime}ms")
    }
}