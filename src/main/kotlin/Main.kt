import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import game.Direction
import game.SnakeGame
import game.State
import ui.theme.SnakeTheme

@Composable
fun App() = SnakeTheme {
    val scope = rememberCoroutineScope()
    val game = SnakeGame(scope, 21, 21)
    val state = game.state.collectAsState(null).value

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Scaffold(
            modifier = Modifier.onKeyEvent { event ->
                val key = event.key
                val dir = when (key) {
                    Key.W -> Direction.Up
                    Key.S -> Direction.Down
                    Key.A -> Direction.Left
                    Key.D -> Direction.Right
                    else -> Direction.None
                }
                if (dir != Direction.None) game.dir = dir
//                println(key)
                false
            },
            topBar = { Navbar(game) },
            content = { state?.let { GameBoard(300, state) } }
        )
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
fun GameBoard(size: Int, state: State) {
    val cols = state.cols
    val rows = state.rows

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Game content
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .border(2.dp, MaterialTheme.colors.onSurface)
                .size(size.dp)
        ) {
            // Draw Snake
            for ((x, y) in state.snake) {
                Box(
                    modifier = Modifier
                        .offset(x.dp * size / cols, y.dp * size / rows)
                        .size(size.dp / cols, size.dp / rows)
                        .background(MaterialTheme.colors.primary)
                )
            }

            // Draw Food
            for ((x, y) in state.food) {
                Box(
                    modifier = Modifier
                        .offset(x.dp * size / cols, y.dp * size / rows)
                        .size(size.dp / cols, size.dp / rows)
                        .background(Color.Red)
                )
            }
        }
    }
}

fun main() = application {
    Window(
        title = "Snake Game",
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}
