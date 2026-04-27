package com.datrooo.snake.core.engine

import com.datrooo.snake.core.model.Coord
import com.datrooo.snake.core.model.GameState
import com.datrooo.snake.core.model.Snake

class FoodSpawner(
    private val random: RandomProvider = KotlinRandomProvider()
) {

    fun ensureFood(state: GameState): Set<Coord> {
        val targetFoodCount = state.config.getTotalFood(
            playersCount = state.aliveSnakes.size
        )

        if (state.foods.size >= targetFoodCount) {
            return state.foods
        }

        val occupiedCells = state.snakes.values
            .flatMap { it.points }
            .toSet()

        val foods = state.foods.toMutableSet()

        val freeCells = buildList {
            for (y in 0 until state.config.height) {
                for (x in 0 until state.config.width) {
                    val coord = Coord(x, y)

                    if (coord !in occupiedCells && coord !in foods) {
                        add(coord)
                    }
                }
            }
        }.toMutableList()

        while (foods.size < targetFoodCount && freeCells.isNotEmpty()) {
            val index = random.nextInt(freeCells.size)
            val coord = freeCells.removeAt(index)
            foods += coord
        }

        return foods.toSet()
    }

    fun spawnFromDeadSnake(
        currentFoods: Set<Coord>,
        deadSnake: Snake,
        deadFoodProbability: Float
    ): Set<Coord> {
        val foods = currentFoods.toMutableSet()

        for (coord in deadSnake.points) {
            if (random.nextFloat() <= deadFoodProbability) {
                foods += coord
            }
        }

        return foods.toSet()
    }
}