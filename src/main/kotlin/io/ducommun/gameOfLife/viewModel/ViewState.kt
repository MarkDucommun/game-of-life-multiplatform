package io.ducommun.gameOfLife.viewModel

interface ViewState {
    var fps: Int
    var board: GameOfLifeViewModel.Dimensions
    var canvas: GameOfLifeViewModel.Dimensions
    var aliveColor: Int
    var deadColor: Int
    var useGradient: Boolean
}