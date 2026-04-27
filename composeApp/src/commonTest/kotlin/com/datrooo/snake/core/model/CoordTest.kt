package com.datrooo.snake.game.model

import com.datrooo.snake.core.model.Coord
import com.datrooo.snake.core.model.Direction
import kotlin.test.Test
import kotlin.test.assertEquals

class CoordTest {

    @Test
    fun coordMovesUp() {
        val coord = Coord(5, 5)

        assertEquals(
            expected = Coord(5, 4),
            actual = coord.move(Direction.Up)
        )
    }

    @Test
    fun coordWrapsFromLeftToRight() {
        val coord = Coord(-1, 5)

        assertEquals(
            expected = Coord(39, 5),
            actual = coord.wrap(width = 40, height = 30)
        )
    }

    @Test
    fun coordWrapsFromTopToBottom() {
        val coord = Coord(10, -1)

        assertEquals(
            expected = Coord(10, 29),
            actual = coord.wrap(width = 40, height = 30)
        )
    }

    @Test
    fun coordWrapsFromRightToLeft() {
        val coord = Coord(40, 5)

        assertEquals(
            expected = Coord(0, 5),
            actual = coord.wrap(width = 40, height = 30)
        )
    }

    @Test
    fun coordWrapsFromBottomToTop() {
        val coord = Coord(10, 30)

        assertEquals(
            expected = Coord(10, 0),
            actual = coord.wrap(width = 40, height = 30)
        )
    }
}