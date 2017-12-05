package io.ducommun.gameOfLife.parser

import io.ducommun.gameOfLife.Coordinate
import io.ducommun.gameOfLife.Plane
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class RunLengthEncodedParserTest {

    val parser = RunLengthEncodedParser()

    fun assertParses(input: String, output: Plane) {
        val plane = parser.parseString(input)
//        assertEquals(emptySet(), output.subtract(plane))
//        assertEquals(emptySet(), plane.subtract(output))
        assertEquals(output.size, plane.size)
        assertEquals(output.toList().sortedBy { it.x*100 + it.y }, plane.toList().sortedBy { it.x*100 + it.y })
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

    @Test
    fun numbers_repeat() {
        assertParses(
                input = """
                    x = 3, y = 1
                    3o!
                """.trimIndent(),
                output = Plane(setOf(
                        Coordinate(x = -1, y = 0),
                        Coordinate(x = 0, y = 0),
                        Coordinate(x = 1, y = 0)
                ))
        )
    }

    @Test
    fun numbers_with_multiple_digits_repeat() {
        assertParses(
                input = """
                    x = 10, y = 1
                    10o!
                """.trimIndent(),
                output = Plane(setOf(
                        Coordinate(x = -5, y = 0),
                        Coordinate(x = -4, y = 0),
                        Coordinate(x = -3, y = 0),
                        Coordinate(x = -2, y = 0),
                        Coordinate(x = -1, y = 0),
                        Coordinate(x = 0, y = 0),
                        Coordinate(x = 1, y = 0),
                        Coordinate(x = 2, y = 0),
                        Coordinate(x = 3, y = 0),
                        Coordinate(x = 4, y = 0)
                ))
        )
    }

    @Test
    fun numbers_with_multiple_digits_repeat_next() {
        assertParses(
                input = """
                    x = 5, y = 2
                    3b2o${"$"}o!
                """.trimIndent(),
                output = Plane(setOf(
                        Coordinate(x = 1, y = 0),
                        Coordinate(x = 2, y = 0),
                        Coordinate(x = -2, y = -1)
                ))
        )
    }

    @Test
    fun values_can_be_split_over_multiple_lines() {
        assertParses(
                input = """
                    x = 5, y = 3
                    3b2o${"$"}o
                    o${"$"}bo!
                """.trimIndent(),
                output = Plane(setOf(
                        Coordinate(x = 1, y = 1),
                        Coordinate(x = 2, y = 1),
                        Coordinate(x = -2, y = 0),
                        Coordinate(x = -1, y = 0),
                        Coordinate(x = -1, y = -1)
                ))
        )
    }

    @Test
    fun ignores_comments() {
        assertParses(input = """
            # this is a comment
            # also this
            x = 1, y = 1
            #some more comments
            o!
            #also a comment I guess, can they be here?
        """.trimIndent(),
                output = Plane(setOf(Coordinate(x = 0, y = 0))))
    }

    @Test
    fun accepts_rule_header() {
        assertParses(input = """
            x = 1, y = 1, rule = B3/S23
            o!
        """.trimIndent(),
                output = Plane(setOf(Coordinate(x = 0, y = 0))))
    }

    @Test
    fun number_before_dollar_sign_makes_multiple_empty_lines() {

        assertParses(input = """
            x = 1, y = 3, rule = B3/S23
            o2${'$'}o!
        """.trimIndent(),
                output = Plane(setOf(
                        Coordinate(x = 0, y = 1),
                        Coordinate(x = 0, y = -1)
                )))
    }

    @Test
    fun parses_a_switchengine() {
        assertParses(input = """
            #N Switch engine
            #O Charles Corderman
            #C A methuselah with lifespan 3911 that can be used to make c/12 diagonal puffers and spaceships.
            #C www.conwaylife.com/wiki/index.php?title=Switch_engine
            x = 6, y = 4, rule = B3/S23
            bobo2b${'$'}o5b${'$'}bo2bob${'$'}3b3o!
        """.trimIndent(), output = """-2 -2
0 -2
-3 -1
-2 0
1 0
0 1
1 1
2 1""".split("\n").map { line ->
            val coords = line.split(" ")
            Coordinate(coords[0].toShort(), 0.minus(coords[1].toShort()).minus(1).toShort())
        }.toSet().let(::Plane))
    }
}
