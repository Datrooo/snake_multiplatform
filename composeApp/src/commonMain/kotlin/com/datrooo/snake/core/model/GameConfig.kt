package com.datrooo.snake.core.model

data class GameConfig(
    val width: Int = 40,
    val height: Int = 30,
    val foodStatic: Int = 10,
    val foodPerPlayer: Int = 3,
    val stateDelayMs: Int = 1000,
    val deadFoodProb: Float = 0.1f,
    val pingDelayMs: Int = 1000,
    val nodeTimeoutMs: Int = 3000
) {
    init {
        require(width > 0) {"width must be positive"}
        require(height > 0) {"height must be positive"}
        require(foodStatic >= 0) {"static food must be not negative"}
        require(foodPerPlayer >= 0) {"food per player must be not negative"}
        require(stateDelayMs > 0) {"state delay must be positive"}
        require(deadFoodProb in 0f..1f) {"dead food probability must be in 0 - 1"}
        require(pingDelayMs > 0) {"ping delay must be positive"}
        require(nodeTimeoutMs > 0) {"node timeout must be positive"}
    }

    fun getTotalFood(playersCount: Int) : Int = foodStatic + foodPerPlayer * playersCount
}
