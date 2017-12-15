package io.ducommun.gameOfLife.viewModel

data class MutableViewState(
    override var fps: Int,
    override var board: GameOfLifeViewModel.Dimensions,
    override var canvas: GameOfLifeViewModel.Dimensions,
    override var aliveColor: Int,
    override var deadColor: Int,
    override var useGradient: Boolean
): ViewState
