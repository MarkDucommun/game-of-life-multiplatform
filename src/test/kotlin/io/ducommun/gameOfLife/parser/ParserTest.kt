package io.ducommun.gameOfLife.parser

import io.ducommun.gameOfLife.Coordinate
import io.ducommun.gameOfLife.Plane
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTest {

    val parser = Parser()

    fun assertParses(input: String, output: Plane) {
        assertEquals(output, parser.parseString(input))
    }

    @Test
    fun parses_an_alive_cell() {

        assertParses(
                input = """
                    x = 1, y = 1
                    o!
                """.trimIndent(),
                output = Plane(setOf(
                        Coordinate(x = 0, y = 0)
                ))
        )
    }

    @Test
    fun parses_multiple_cells() {

        assertParses(
                input = """
                    x = 3, y = 1
                    ooo!
                """.trimIndent(),
                output = Plane(setOf(
                        Coordinate(x = -1, y = 0),
                        Coordinate(x = 0, y = 0),
                        Coordinate(x = 1, y = 0)
                ))
        )
    }

    @Test
    fun skips_dead_cells() {

        assertParses(
                input = """
                    x = 3, y = 1
                    obo!
                """.trimIndent(),
                output = Plane(setOf(
                        Coordinate(x = -1, y = 0),
                        Coordinate(x = 1, y = 0)
                ))
        )
    }

    @Test
    fun supports_multiple_lines() {
        assertParses(
                input = """
                    x = 1, y = 2
                    o${"$"}o!
                """.trimIndent(),
                output = Plane(setOf(
                        Coordinate(x = 0, y = 0),
                        Coordinate(x = 0, y = -1)
                ))
        )
    }

    @Test
    fun is_correctly_vertically_oriented() {
        assertParses(
                input = """
                    x = 3, y = 2
                    ooo${"$"}obo!
                """.trimIndent(),
                output = Plane(setOf(
                        Coordinate(x = -1, y = 0),
                        Coordinate(x = 0, y = 0),
                        Coordinate(x = 1, y = 0),
                        Coordinate(x = -1, y = -1),
                        Coordinate(x = 1, y = -1)
                ))
        )
    }

    @Test
    fun three_by_three() {

        assertParses(
                input = """
                    x = 3, y = 3
                    obb${"$"}bob${"$"}bbo!
                """.trimIndent(),
                output = Plane(setOf(
                        Coordinate(x = -1, y = 1),
                        Coordinate(x = 0, y = 0),
                        Coordinate(x = 1, y = -1)
                ))
        )
    }
}
