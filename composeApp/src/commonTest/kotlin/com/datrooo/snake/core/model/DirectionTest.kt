package com.datrooo.snake.core.model

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DirectionTest {

    @Test
    fun oppositeDirectionsAreDetected() {
        assertTrue(Direction.Up.isOpposite(Direction.Down))
        assertTrue(Direction.Down.isOpposite(Direction.Up))
        assertTrue(Direction.Left.isOpposite(Direction.Right))
        assertTrue(Direction.Right.isOpposite(Direction.Left))
    }

    @Test
    fun nonOppositeDirectionsAreNotDetectedAsOpposite() {
        assertFalse(Direction.Up.isOpposite(Direction.Left))
        assertFalse(Direction.Up.isOpposite(Direction.Right))
        assertFalse(Direction.Left.isOpposite(Direction.Down))
    }
}