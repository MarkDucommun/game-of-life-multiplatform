package io.ducommun.gameOfLife

val ACORN = Plane(setOf(
        Coordinate(x = 0, y = 0),
        Coordinate(x = 1, y = 0),
        Coordinate(x = 4, y = 0),
        Coordinate(x = 5, y = 0),
        Coordinate(x = 6, y = 0),
        Coordinate(x = 3, y = 1),
        Coordinate(x = 1, y = 2)
))

val R_PENTAMINO = Plane(setOf(
        Coordinate(x = 0, y = 1),
        Coordinate(x = 0, y = 0),
        Coordinate(x = 0, y = -1),
        Coordinate(x = -1, y = 0),
        Coordinate(x = 1, y = 1)
))