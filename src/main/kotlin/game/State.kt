package game

data class State(
    val rows: Int, val cols: Int,
    val snake: List<Pair<Int, Int>>,
    val food: Pair<Int, Int>,
    val running: Running
)

enum class Running {
    NOT_STARTED, RUNNING, GAME_OVER
}
