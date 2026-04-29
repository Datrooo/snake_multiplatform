package com.datrooo.snake.core.model

enum class Direction
{
    Up,
    Down,
    Left,
    Right;

    fun opposite() : Direction = when(this) {
        Up -> Down
        Down -> Up
        Left -> Right
        Right -> Left
    }

    fun isOpposite(other: Direction) : Boolean = other == opposite()
}
