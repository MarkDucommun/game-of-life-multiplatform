package io.ducommun.gameOfLife.parser

import io.ducommun.gameOfLife.Coordinate
import io.ducommun.gameOfLife.Plane
import io.ducommun.gameOfLife.HashSetPlane

sealed class Token {
    data class Digit(val value: String) : Token()

    object Alive : Token()

    object Dead : Token()

    object NewRow: Token()
}

class RunLengthEncodedParser {

    fun parseString(input: String): Plane {

        val lines = input.split("\n").filterNot { it.first() == '#' }

        val mapOfOptions = lines.first().split(",").map {
            it.split("=").run { first().trim() to last().trim() }
        }.toMap()

        val width = mapOfOptions["x"]!!.toInt()
        val height = mapOfOptions["y"]!!.toInt()
        val xOffset = width / 2
        val yOffset = height / 2 - if (height.rem(2) == 0) 1 else 0

        val lineWithoutBang = lines.drop(1).joinToString(separator = "").dropLast(1)

        val tokens = mutableListOf<Token>()

        lineWithoutBang.forEach {
            val isDigit = Regex("[0-9]").matches(it.toString())
            val lastToken = tokens.lastOrNull()
            if (isDigit && lastToken is Token.Digit) {
                tokens[tokens.count() - 1] = Token.Digit(value = lastToken.value + it)
            } else {
                when {
                    isDigit -> tokens.add(Token.Digit(value = it.toString()))
                    it == 'o' -> tokens.add(Token.Alive)
                    it == 'b' -> tokens.add(Token.Dead)
                    it == '$' -> tokens.add(Token.NewRow)
                }
            }
        }

        var xPosition = -xOffset
        var yPosition = -yOffset

        val coordinates = mutableListOf<Coordinate>()

        var number: Int? = null

        tokens.forEach { token ->
            when (token) {
                is Token.Digit -> number = token.value.toInt()
                is Token.NewRow -> {
                    val count = number ?: 1
                    yPosition += count
                    number = null
                    xPosition = -xOffset
                }
                else -> {
                    val count = number ?: 1
                    if (token is Token.Alive) {
                        (xPosition until xPosition + count)
                                .map { Coordinate(x = it.toShort(), y = 0.minus(yPosition.toShort()).toShort()) }
                                .let { coordinates.addAll(it) }
                    }
                    xPosition += count
                    number = null
                }
            }
        }

        return HashSetPlane(livingCells = coordinates.toSet())
    }
}