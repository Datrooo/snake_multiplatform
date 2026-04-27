package com.datrooo.snake.core.model

data class Coord(
    val x: Int,
    val y: Int
) {
    fun moved(direction: Direction): Coord = when (direction) {
        Direction.Up -> copy(y = y - 1)
        Direction.Down -> copy(y = y + 1)
        Direction.Left -> copy(x = x - 1)
        Direction.Right -> copy(x = x + 1)
    }

    fun wrapped(width: Int, height: Int): Coord {
        val newX = ((x % width) + width) % width
        val newY = ((y % height) + height) % height
        return Coord(newX, newY)
    }

    fun movedWrapped(direction: Direction, width: Int, height: Int): Coord {
        return moved(direction).wrapped(width, height)
    }
}
