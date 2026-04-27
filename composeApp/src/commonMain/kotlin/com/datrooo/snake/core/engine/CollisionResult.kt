package com.datrooo.snake.core.engine

import com.datrooo.snake.core.model.PlayerId

data class CollisionResult(
    val deadSnakeIds: Set<PlayerId>,
    val killPoints: Map<PlayerId, Int>
)
