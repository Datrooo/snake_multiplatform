package com.datrooo.snake.core.engine

import com.datrooo.snake.core.model.Direction
import com.datrooo.snake.core.model.PlayerId

sealed interface GameCommand {

    data class ChangeDirection(
        val playerId: PlayerId,
        val direction: Direction
    ) : GameCommand
}
