package game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import androidx.compose.ui.input.key.Key

class SnakeGame(val scope: CoroutineScope, val rows: Int, val cols: Int) {
    private var running = Running.NOT_STARTED
    private val mutex = Mutex()
    private var dir = Direction.Up
        set(value) {
            scope.launch {
                mutex.withLock {
                    field = value
                }
            }
        }


    private var head = cols / 2 to rows / 2
    private var snake = listOf(head)
    private var food = Pair(0, 0)

    private val mutableState = MutableStateFlow(State(rows, cols, snake, food, running))
    val state: Flow<State> = mutableState

    init {
        new()
    }

    fun new() {
        stop()
        running = Running.RUNNING
        head = cols / 2 to rows / 2
        snake = mutableListOf(head)
        dir = Direction.None
        food = (0..<cols).random() to (0..<rows).random()

        mutableState.update {
            it.copy(
                snake = snake,
                food = food,
                running = running
            )
        }

        start()
    }

    private fun tick() {
        move(dir)

        mutableState.update {
            it.copy(
                snake = snake,
                food = food,
                running = running
            )
        }
    }

    private fun move(dir: Direction) {
        var (x, y) = head
        when (dir) {
            Direction.Up -> y--
            Direction.Down -> y++
            Direction.Left -> x--
            Direction.Right -> x++
            Direction.None -> {}
        }

        head = x to y
        if (head.first !in 0 until cols || head.second !in 0 until rows || head in snake.drop(1)) {
            stop()
            return
        }

        snake = listOf(head) + snake.run { if (head != food) dropLast(1) else this }
        if (head == food) {
            food = (0..<cols).random() to (0..<rows).random()
        }
    }

    fun onInput(key: Key) {
        when (key) {
            Key.Spacebar -> if (running == Running.GAME_OVER) new()
            else -> {}
        }

        val dir = when (key) {
            Key.W, Key.DirectionUp -> Direction.Up
            Key.S, Key.DirectionDown -> Direction.Down
            Key.A, Key.DirectionLeft -> Direction.Left
            Key.D, Key.DirectionRight -> Direction.Right
            else -> Direction.None
        }
        if (dir == Direction.None) return
        if (dir == this.dir.opposite()) return
        if (dir == this.dir) return

        println(dir)
        this.dir = dir
    }

    private fun start() = scope.launch {
        // Game loop
        while (running == Running.RUNNING) {
            delay(1000L / 10)
            tick()
        }
    }

    private fun stop() {
        running = Running.GAME_OVER
        mutableState.update {
            it.copy(running = running)
        }
    }
}

enum class Direction {
    Up, Down, Left, Right, None;
    fun opposite() = when (this) {
        Up -> Down
        Down -> Up
        Left -> Right
        Right -> Left
        None -> None
    }
}