package io.ducommun.gameOfLife.viewModel

data class ImmutableActions(
    override val drawPlane: (IntArray) -> Unit,
    override val drawPlaneDiff: (List<Rectangle>) -> Unit,
    override val updateStats: (GameOfLifeViewModel.Stats) -> Unit,
    override val currentTime: () -> Long
) : Actions