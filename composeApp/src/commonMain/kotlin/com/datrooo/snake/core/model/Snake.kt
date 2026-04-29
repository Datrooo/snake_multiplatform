package com.datrooo.snake.core.model
data class Snake(
    val playerId: PlayerId,
    val points: List<Coord>,
    val direction: Direction,
    val nextDirection: Direction = direction,
    val state: SnakeState = SnakeState.Alive
) {
    init {
        require(points.isNotEmpty()) { "Snake must have at least one point" }
    }

    val head: Coord
        get() = points.first()

    val body: List<Coord>
        get() = points.drop(1)

    fun withNextDirection(direction: Direction): Snake {
        if (this.direction.isOpposite(direction)) {
            return this
        }

        return copy(nextDirection = direction)
    }

    fun moved(
        width: Int,
        height: Int,
        grow: Boolean
    ): Snake {
        val newHead = head.movedWrapped(
            direction = nextDirection,
            width = width,
            height = height
        )

        val newPoints = if (grow) {
            listOf(newHead) + points
        } else {
            listOf(newHead) + points.dropLast(1)
        }

        return copy(
            points = newPoints,
            direction = nextDirection
        )
    }

    fun asZombie(): Snake {
        return copy(state = SnakeState.Zombie)
    }
}
