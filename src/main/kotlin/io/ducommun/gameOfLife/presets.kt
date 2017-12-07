package io.ducommun.gameOfLife

import io.ducommun.gameOfLife.parser.RunLengthEncodedParser

expect object PresetStrings {
    val ACORN: String
    val BREEDER_ONE: String
    val FERMAT_PRIME_CALCULATOR: String
    val INFINITE_GLIDER_HOTEL_FOUR: String
    val MAX: String
    val R_PENTOMINO: String
    val R_PENTOMINO_SYNTHESIS: String
    val SPAGHETTI_MONSTER: String
    val TEN_ENGINE_CORDERSHIP: String
    val THREE_ENGINE_CORDERSHIP_GUN: String
    val THREE_ENGINE_CORDERSHIP_RAKE: String
    val TEST_PATTERN: String
}

object Presets {

    private val parser: RunLengthEncodedParser = RunLengthEncodedParser()

    val ACORN: Plane by lazy { parser.parseString(PresetStrings.ACORN) }
    val BREEDER_ONE: Plane by lazy { parser.parseString(PresetStrings.BREEDER_ONE) }
    val FERMAT_PRIME_CALCULATOR: Plane by lazy { parser.parseString(PresetStrings.FERMAT_PRIME_CALCULATOR) }
    val INFINITE_GLIDER_HOTEL_FOUR: Plane by lazy { parser.parseString(PresetStrings.INFINITE_GLIDER_HOTEL_FOUR) }
    val MAX: Plane by lazy { parser.parseString(PresetStrings.MAX) }
    val R_PENTOMINO: Plane by lazy { parser.parseString(PresetStrings.R_PENTOMINO) }
    val R_PENTOMINO_SYNTHESIS: Plane by lazy { parser.parseString(PresetStrings.R_PENTOMINO_SYNTHESIS) }
    val SPAGHETTI_MONSTER: Plane by lazy { parser.parseString(PresetStrings.SPAGHETTI_MONSTER) }
    val TEN_ENGINE_CORDERSHIP: Plane by lazy { parser.parseString(PresetStrings.TEN_ENGINE_CORDERSHIP) }
    val THREE_ENGINE_CORDERSHIP_GUN: Plane by lazy { parser.parseString(PresetStrings.THREE_ENGINE_CORDERSHIP_GUN) }
    val THREE_ENGINE_CORDERSHIP_RAKE: Plane by lazy { parser.parseString(PresetStrings.THREE_ENGINE_CORDERSHIP_RAKE) }
    val TEST_PATTERN: Plane by lazy { parser.parseString(PresetStrings.TEST_PATTERN) }
}