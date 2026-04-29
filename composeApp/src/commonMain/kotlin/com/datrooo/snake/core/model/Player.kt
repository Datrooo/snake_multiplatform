package com.datrooo.snake.core.model

data class Player(
    val id: PlayerId,
    val name: String,
    val score: Int = 0,
    val role: NodeRole = NodeRole.Normal,
    val type: PlayerType = PlayerType.Human,
    val host: String? = null,
    val port: Int? = null
) {
    init {
        require(name.isNotBlank()) { "player name must not be blank" }
        require(score >= 0) { "player score must not be negative" }
    }
}
