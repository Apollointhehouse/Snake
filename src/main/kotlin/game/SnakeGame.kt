package game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SnakeGame(val scope: CoroutineScope, val rows: Int, val cols: Int) {
    private var running = false
    var dir = Direction.Up

    private var head = cols / 2 to rows / 2
    private var snake = mutableListOf(head)
    private var food = emptySet<Pair<Int, Int>>()

    private val mutableState = MutableStateFlow(State(rows, cols, snake, food, head))
    val state: Flow<State> = mutableState

    init {
        scope.launch {
            new()
            start()
        }
    }

    fun new() {
        head = cols / 2 to rows / 2
        snake = mutableListOf(head)
        food = List(6) {
            (0..<cols).random() to (0..<rows).random()
        }.toSet()
    }

    private fun tick() {
        var (x, y) = head
        println(dir)
        when (dir) {
            Direction.Up -> y--
            Direction.Down -> y++
            Direction.Left -> x--
            Direction.Right -> x++
            Direction.None -> {}
        }

        val newHead = x to y
        val newSnake = mutableState.value.snake
            .let { if (it.size > 3) it.drop(1) else it } + newHead

        mutableState.update {
            it.copy(
                head = newHead,
                snake = newSnake,
            )
        }
        println(snake)
    }

    suspend fun start()  {
        // Game loop
        running = true
        while (running) {
            delay(1000L * 1)
            tick()
        }
    }

    fun stop() {
        running = false
    }
}

enum class Direction {
    Up, Down, Left, Right, None
}