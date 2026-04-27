package com.datrooo.snake.core.model

import kotlin.test.Test
import kotlin.test.assertEquals

class SnakeTest {

    @Test
    fun snakeMovesWithoutGrowing() {
        val snake = Snake(
            playerId = PlayerId(1),
            points = listOf(
                Coord(5, 5),
                Coord(5, 6),
                Coord(5, 7)
            ),
            direction = Direction.Up
        )

        val moved = snake.moved(
            width = 40,
            height = 30,
            grow = false
        )

        assertEquals(
            expected = listOf(
                Coord(5, 4),
                Coord(5, 5),
                Coord(5, 6)
            ),
            actual = moved.points
        )
    }

    @Test
    fun snakeMovesAndGrows() {
        val snake = Snake(
            playerId = PlayerId(1),
            points = listOf(
                Coord(5, 5),
                Coord(5, 6),
                Coord(5, 7)
            ),
            direction = Direction.Up
        )

        val moved = snake.moved(
            width = 40,
            height = 30,
            grow = true
        )

        assertEquals(
            expected = listOf(
                Coord(5, 4),
                Coord(5, 5),
                Coord(5, 6),
                Coord(5, 7)
            ),
            actual = moved.points
        )
    }

    @Test
    fun snakeCannotReverseDirection() {
        val snake = Snake(
            playerId = PlayerId(1),
            points = listOf(
                Coord(5, 5),
                Coord(5, 6),
                Coord(5, 7)
            ),
            direction = Direction.Up
        )

        val updated = snake.withNextDirection(Direction.Down)

        assertEquals(
            expected = Direction.Up,
            actual = updated.nextDirection
        )
    }

    @Test
    fun snakeCanTurnLeft() {
        val snake = Snake(
            playerId = PlayerId(1),
            points = listOf(
                Coord(5, 5),
                Coord(5, 6),
                Coord(5, 7)
            ),
            direction = Direction.Up
        )

        val updated = snake.withNextDirection(Direction.Left)

        assertEquals(
            expected = Direction.Left,
            actual = updated.nextDirection
        )
    }
}