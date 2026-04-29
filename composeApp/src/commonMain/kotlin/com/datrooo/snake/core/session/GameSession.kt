package com.datrooo.snake.core.session

import com.datrooo.snake.core.engine.GameCommand
import com.datrooo.snake.core.engine.GameEngine
import com.datrooo.snake.core.model.GameState
import com.datrooo.snake.core.model.PlayerId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GameSession(
    private val engine: GameEngine,
    initialState: GameState,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val sessionScope = CoroutineScope(
        SupervisorJob() + dispatcher
    )

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<GameState> = _state.asStateFlow()

    private val _status = MutableStateFlow(GameSessionStatus.Idle)
    val status: StateFlow<GameSessionStatus> = _status.asStateFlow()

    private val _events = MutableSharedFlow<GameEvent>(
        extraBufferCapacity = 64
    )
    val events: SharedFlow<GameEvent> = _events.asSharedFlow()

    private val commands = MutableSharedFlow<GameCommand>(
        extraBufferCapacity = 64
    )

    private val commandMutex = Mutex()
    private val pendingCommands = mutableListOf<GameCommand>()

    private var commandCollectorJob: Job? = null
    private var gameLoopJob: Job? = null

    fun start() {
        if (_status.value == GameSessionStatus.Running) {
            return
        }

        if (_status.value == GameSessionStatus.Stopped) {
            return
        }

        startCommandCollectorIfNeeded()
        startGameLoopIfNeeded()
        _status.value = GameSessionStatus.Running
    }

    fun pause() {
        if (_status.value == GameSessionStatus.Running) {
            _status.value = GameSessionStatus.Paused
        }
    }

    fun resume() {
        if (_status.value == GameSessionStatus.Paused) {
            _status.value = GameSessionStatus.Running
        }
    }

    fun stop() {
        if (_status.value == GameSessionStatus.Stopped) {
            return
        }

        gameLoopJob?.cancel()
        commandCollectorJob?.cancel()

        gameLoopJob = null
        commandCollectorJob = null

        _status.value = GameSessionStatus.Stopped
        sessionScope.cancel()
    }

    fun sendCommand(command: GameCommand): Boolean {
        return commands.tryEmit(command)
    }

    suspend fun emitCommand(command: GameCommand) {
        commands.emit(command)
    }

    private fun startCommandCollectorIfNeeded() {
        if (commandCollectorJob?.isActive == true) {
            return
        }

        commandCollectorJob = sessionScope.launch {
            commands.collect { command ->
                commandMutex.withLock {
                    pendingCommands += command
                }
            }
        }
    }

    private fun startGameLoopIfNeeded() {
        if (gameLoopJob?.isActive == true) {
            return
        }

        gameLoopJob = sessionScope.launch {
            while (isActive) {
                when (_status.value) {
                    GameSessionStatus.Running -> {
                        tick()
                        delay(_state.value.config.stateDelayMs)
                    }

                    GameSessionStatus.Idle,
                    GameSessionStatus.Paused -> {
                        delay(PAUSED_LOOP_DELAY_MS)
                    }

                    GameSessionStatus.Stopped -> {
                        break
                    }
                }
            }
        }
    }

    private suspend fun tick() {
        val oldState = _state.value
        val commandsForTick = commandMutex.withLock {
            pendingCommands.toList().also { pendingCommands.clear() }
        }

        val newState = engine.reduce(
            state = oldState,
            commands = commandsForTick
        )

        _state.value = newState
        emitEvents(oldState, newState)
    }

    private suspend fun emitEvents(
        oldState: GameState,
        newState: GameState
    ) {
        val removedSnakeIds: Set<PlayerId> = oldState.snakes.keys - newState.snakes.keys

        for (playerId in removedSnakeIds) {
            _events.emit(GameEvent.SnakeDied(playerId))
        }

        val removedPlayerIds: Set<PlayerId> = oldState.players.keys - newState.players.keys

        for (playerId in removedPlayerIds) {
            _events.emit(GameEvent.PlayerRemoved(playerId))
        }

        if (oldState.players.isNotEmpty() && newState.players.isEmpty()) {
            _events.emit(GameEvent.GameOver)
        }
    }

    companion object {
        private const val PAUSED_LOOP_DELAY_MS = 50L
    }
}
