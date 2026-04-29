package com.datrooo.snake.core.engine

import com.datrooo.snake.core.model.GameState
import com.datrooo.snake.core.model.Player
import com.datrooo.snake.core.model.PlayerId
import com.datrooo.snake.core.model.Snake
import com.datrooo.snake.core.model.SnakeState

class GameEngine(
    private val foodSpawner: FoodSpawner = FoodSpawner(),
    private val collisionResolver: CollisionResolver = CollisionResolver()
) {

    fun reduce(
        state: GameState,
        commands: List<GameCommand>
    ): GameState {
        val config = state.config

        var snakes = applyCommands(
            snakes = state.snakes,
            commands = commands
        )

        val activeSnakes = snakes.values.filter {
            it.state == SnakeState.Alive || it.state == SnakeState.Zombie
        }

        val ateFood = activeSnakes.associate { snake ->
            val nextHead = snake.head.movedWrapped(
                direction = snake.nextDirection,
                width = config.width,
                height = config.height
            )

            snake.playerId to (nextHead in state.foods)
        }

        val eatenFoodCells = activeSnakes
            .mapNotNull { snake ->
                val nextHead = snake.head.movedWrapped(
                    direction = snake.nextDirection,
                    width = config.width,
                    height = config.height
                )

                if (nextHead in state.foods) nextHead else null
            }
            .toSet()

        var players = state.players.mapValues { entry ->
            val playerId = entry.key
            val player = entry.value
            val snake = snakes[playerId]

            val foodScore = if (
                snake?.state == SnakeState.Alive &&
                ateFood[playerId] == true
            ) {
                1
            } else {
                0
            }

            player.copy(score = player.score + foodScore)
        }

        snakes = snakes.mapValues { entry ->
            val snake = entry.value

            if (snake.state == SnakeState.Alive || snake.state == SnakeState.Zombie) {
                snake.moved(
                    width = config.width,
                    height = config.height,
                    grow = ateFood[snake.playerId] == true
                )
            } else {
                snake
            }
        }

        var foods = state.foods - eatenFoodCells

        val collisionResult = collisionResolver.resolve(snakes)

        players = players.mapValues { entry ->
            val playerId = entry.key
            val player = entry.value
            val killScore = collisionResult.killPoints[playerId] ?: 0

            player.copy(score = player.score + killScore)
        }

        val deadSnakes = collisionResult.deadSnakeIds.mapNotNull { deadId ->
            snakes[deadId]
        }

        for (deadSnake in deadSnakes) {
            foods = foodSpawner.spawnFromDeadSnake(
                currentFoods = foods,
                deadSnake = deadSnake,
                deadFoodProbability = config.deadFoodProbability
            )
        }

        snakes = snakes - collisionResult.deadSnakeIds
        players = players - collisionResult.deadSnakeIds

        val stateBeforeFoodRefill = state.copy(
            stateOrder = state.stateOrder + 1,
            players = players,
            snakes = snakes,
            foods = foods
        )

        val refilledFoods = foodSpawner.ensureFood(stateBeforeFoodRefill)

        return stateBeforeFoodRefill.copy(
            foods = refilledFoods
        )
    }

    fun addPlayer(
        state: GameState,
        player: Player,
        snake: Snake
    ): GameState {
        require(player.id == snake.playerId) {
            "Player id and snake player id must be equal"
        }

        require(player.id !in state.players) {
            "Player already exists: ${player.id.value}"
        }

        require(player.id !in state.snakes) {
            "Snake already exists for player: ${player.id.value}"
        }

        val newState = state.copy(
            players = state.players + (player.id to player),
            snakes = state.snakes + (snake.playerId to snake)
        )

        return newState.copy(
            foods = foodSpawner.ensureFood(newState)
        )
    }

    fun removePlayer(
        state: GameState,
        playerId: PlayerId
    ): GameState {
        val snake = state.snakes[playerId]

        val updatedSnakes = if (snake != null && snake.state == SnakeState.Alive) {
            state.snakes + (playerId to snake.asZombie())
        } else {
            state.snakes
        }

        return state.copy(
            players = state.players - playerId,
            snakes = updatedSnakes
        )
    }

    private fun applyCommands(
        snakes: Map<PlayerId, Snake>,
        commands: List<GameCommand>
    ): Map<PlayerId, Snake> {
        var updatedSnakes = snakes

        for (command in commands) {
            when (command) {
                is GameCommand.ChangeDirection -> {
                    val snake = updatedSnakes[command.playerId]

                    if (snake != null && snake.state == SnakeState.Alive) {
                        updatedSnakes = updatedSnakes + (
                            command.playerId to snake.withNextDirection(command.direction)
                        )
                    }
                }
            }
        }

        return updatedSnakes
    }
}
