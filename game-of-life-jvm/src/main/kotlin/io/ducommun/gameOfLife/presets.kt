@file:JvmName("spreset")
package io.ducommun.gameOfLife

import java.io.File

actual object PresetStrings {

    private val String.fileContents: String get() = let(::File).readBytes().let { String(it) }

    private val String.patternFromFile: String get() = "./game-of-life-jvm/src/main/resources/patterns/$this.rle".fileContents

    actual val ACORN: String by lazy  { "acorn".patternFromFile }
    actual val BREEDER_ONE: String by lazy { "breeder_one".patternFromFile }
    actual val FERMAT_PRIME_CALCULATOR: String by lazy { "fermat_prime_calculator".patternFromFile }
    actual val FROTHING_PUFFER: String by lazy { "frothing_puffer".patternFromFile }
    actual val INFINITE_GLIDER_HOTEL_FOUR: String by lazy { "infinite_glider_hotel_four".patternFromFile }
    actual val MAX: String by lazy { "max".patternFromFile }
    actual val NOAHS_ARK: String by lazy { "noahs_ark".patternFromFile }
    actual val R_PENTOMINO: String by lazy { "r_pentomino".patternFromFile }
    actual val R_PENTOMINO_SYNTHESIS: String by lazy { "r_pentomino_synthesis".patternFromFile }
    actual val SPAGHETTI_MONSTER: String by lazy { "spaghetti_monster".patternFromFile }
    actual val TEN_ENGINE_CORDERSHIP: String by lazy { "ten_engine_cordership".patternFromFile }
    actual val THREE_ENGINE_CORDERSHIP_GUN: String by lazy { "three_engine_cordership_gun".patternFromFile }
    actual val THREE_ENGINE_CORDERSHIP_RAKE: String by lazy { "three_engine_cordership_rake".patternFromFile }
    actual val TEST_PATTERN: String by lazy { "test_pattern".patternFromFile }
}