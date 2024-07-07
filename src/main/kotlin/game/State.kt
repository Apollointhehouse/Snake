package game

data class State(
    val rows: Int, val cols: Int,
    val snake: List<Pair<Int, Int>>,
    val food: Set<Pair<Int, Int>>,
    val head: Pair<Int, Int>
)
