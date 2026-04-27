package com.datrooo.snake.core.model

data class GameState(
    val config: GameConfig,
    val stateOrder: Int = 0,
    val players: Map<PlayerId, Player> = emptyMap(),
    val snakes: Map<PlayerId, Snake> = emptyMap(),
    val foods: Set<Coord> = emptySet()
) {
    val aliveSnakes: List<Snake>
        get() = snakes.values.filter { it.state == SnakeState.Alive }

    val isEmpty: Boolean
        get() = players.isEmpty() && snakes.isEmpty()

    fun playerScore(playerId: PlayerId): Int {
        return players[playerId]?.score ?: 0
    }
}