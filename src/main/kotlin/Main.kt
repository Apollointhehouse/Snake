import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import game.Running
import game.SnakeGame
import game.State
import ui.theme.SnakeTheme

@Composable
fun App(game: SnakeGame) = SnakeTheme {
    val state = game.state.collectAsState(null).value

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Scaffold(
            modifier = Modifier.onKeyEvent { event ->
                val key = event.key
                game.onInput(key)
                false
            },
            topBar = { Navbar(game) },
        ) {
            state ?: return@Scaffold
            val head = state.snake.first()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Score: ${state.snake.size - 1}")
                Text("X: ${head.first}, Y: ${head.second}")
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when (state.running) {
                    Running.NOT_STARTED -> {
                        StartGame()
                    }
                    Running.RUNNING -> {
                        GameBoard(300, state)
                    }
                    Running.GAME_OVER -> {
                        GameOver()
                    }
                }
            }

        }
    }
    
}

@Composable
fun Navbar(game: SnakeGame) {
    TopAppBar(
        title = { Text("Snake Game") },
        actions = {
            Button(onClick = {
                game.new()
            }) {
                Text("New Game")
            }
        }
    )
}

@Composable
fun BoxScope.StartGame() {
    Column(
        modifier = Modifier.align(Alignment.Center)
    ) {
        Text(
            "Start Game",
            style = MaterialTheme.typography.h4,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Press 'Space' to start the game",
            style = MaterialTheme.typography.h6,
            color = Color.White
        )
    }
}

@Composable
fun BoxScope.GameOver() {
    Column(
        modifier = Modifier.align(Alignment.Center)
    ) {
        Text(
            "Game Over",
            style = MaterialTheme.typography.h4,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Press 'Space' to start a new game",
            style = MaterialTheme.typography.h6,
            color = Color.White
        )
    }

}


@Composable
fun BoxScope.GameBoard(size: Int, state: State) {
    val cols = state.cols
    val rows = state.rows

    // Game content
    Box(
        modifier = Modifier
            .align(Alignment.Center)
            .border((2).dp, MaterialTheme.colors.onSurface)
            .size(size.dp)
    ) {
        // Draw Snake
        for ((x, y) in state.snake) {
            Box(
                modifier = Modifier
                    .offset(x.dp * size / cols, y.dp * size / rows)
                    .size(size.dp / cols - 1.dp, size.dp / rows - 1.dp)
                    .background(MaterialTheme.colors.primary)
            )
        }

        // Draw Food
        state.food.let { (x, y) ->
            Box(
                modifier = Modifier
                    .offset(x.dp * size / cols, y.dp * size / rows)
                    .size(size.dp / cols, size.dp / rows)
                    .background(Color.Red)
            )
        }
    }
}

fun main() = application {
    val scope = rememberCoroutineScope()
    val game = SnakeGame(scope, 21, 21)

    Window(
        title = "Snake Game",
        onCloseRequest = ::exitApplication
    ) {
        App(game)
    }
}
