package io.ducommun.gameOfLife.parser

import io.ducommun.gameOfLife.Coordinate
import io.ducommun.gameOfLife.Plane

class Parser {

    fun parseString(input: String): Plane {

        val lines = input.split("\n")

        val smpa = lines.first().split(",").map {
            val blah = it.split("=")
            blah.first().trim() to blah.last().trim().toInt()
        }.toMap()

        val xOffset = smpa["x"]!! / 2
        val yOffset = smpa["y"]!! / 2

        val lineWithoutBang = lines[1].dropLast(1)

        val rows = lineWithoutBang.split("$").reversed()


        return rows.mapIndexed { y, row ->

            row
                    .mapIndexed { x, value -> Coordinate(x = (x - xOffset).toShort(), y = ((y - yOffset)).toShort()).takeIf { value == 'o' } }
                    .filterNotNull()
        }
                .flatten()
                .toSet()
                .let(::Plane)
    }
}