package io.ducommun.gameOfLife.viewModel

interface Actions {
    val drawPlane: (IntArray) -> Unit
    val drawPlaneDiff: (List<Rectangle>) -> Unit
    val updateStats: (GameOfLifeViewModel.Stats) -> Unit
    val currentTime: () -> Long
}