package com.datrooo.snake.core.session

import com.datrooo.snake.core.model.PlayerId

sealed interface GameEvent {
    data class SnakeDied(
        val playerId: PlayerId
    ) : GameEvent

    data class PlayerRemoved(
        val playerId: PlayerId
    ) : GameEvent

    data object GameOver : GameEvent
}
