package com.datrooo.snake.core.engine
import kotlin.random.Random

interface RandomProvider {
    fun nextInt(until: Int): Int
    fun nextFloat(): Float
}

class KotlinRandomProvider(
    private val random: Random = Random.Default
) : RandomProvider {

    override fun nextInt(until: Int): Int {
        return random.nextInt(until)
    }

    override fun nextFloat(): Float {
        return random.nextFloat()
    }
}