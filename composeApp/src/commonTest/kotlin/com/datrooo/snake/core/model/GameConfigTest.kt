package com.datrooo.snake.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GameConfigTest {

    @Test
    fun targetFoodCountDependsOnAliveSnakes() {
        val config = GameConfig(
            foodStatic = 1,
            foodPerPlayer = 2
        )

        assertEquals(
            expected = 7,
            actual = config.getTotalFood(playersCount = 3)
        )
    }

    @Test
    fun invalidWidthIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            GameConfig(width = 0)
        }
    }

    @Test
    fun invalidDeadFoodProbabilityIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            GameConfig(deadFoodProb = 1.5f)
        }
    }
}