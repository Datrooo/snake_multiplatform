package com.datrooo.snake.core.engine

import com.datrooo.snake.core.model.Coord
import com.datrooo.snake.core.model.PlayerId
import com.datrooo.snake.core.model.Snake
import com.datrooo.snake.core.model.SnakeState
import com.datrooo.snake.game.engine.CollisionResult

class CollisionResolver {

    fun resolve(snakes: Map<PlayerId, Snake>): CollisionResult {
        val activeSnakes = snakes.values.filter {
            it.state == SnakeState.Alive || it.state == SnakeState.Zombie
        }

        val bodyOwner = mutableMapOf<Coord, PlayerId>()

        for (snake in activeSnakes) {
            for (bodyCell in snake.body) {
                bodyOwner[bodyCell] = snake.playerId
            }
        }

        val headsAt = activeSnakes
            .groupBy { it.head }
            .mapValues { entry ->
                entry.value.map { it.playerId }
            }

        val headOnDeaths = headsAt.values
            .filter { playerIds -> playerIds.size > 1 }
            .flatten()
            .toSet()

        val deadSnakeIds = headOnDeaths.toMutableSet()

        for (snake in activeSnakes) {
            if (snake.head in bodyOwner) {
                deadSnakeIds += snake.playerId
            }
        }

        val killPoints = mutableMapOf<PlayerId, Int>()

        for (victim in activeSnakes) {
            val victimId = victim.playerId

            if (victimId !in deadSnakeIds) {
                continue
            }

            if (victimId in headOnDeaths) {
                continue
            }

            val killerId = bodyOwner[victim.head] ?: continue

            if (killerId == victimId) {
                continue
            }

            if (killerId in deadSnakeIds) {
                continue
            }

            killPoints[killerId] = killPoints.getOrDefault(killerId, 0) + 1
        }

        return CollisionResult(
            deadSnakeIds = deadSnakeIds.toSet(),
            killPoints = killPoints.toMap()
        )
    }
}