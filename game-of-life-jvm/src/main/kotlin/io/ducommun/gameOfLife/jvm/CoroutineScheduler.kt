package io.ducommun.gameOfLife.jvm

import io.ducommun.gameOfLife.viewModel.Scheduler
import javafx.application.Platform
import kotlinx.coroutines.experimental.launch

class CoroutineScheduler : Scheduler {

    override fun immediately(task: () -> Unit) {
        launch { task() }
    }

    override fun delay(milliseconds: Int, task: () -> Unit) {
        launch {
            kotlinx.coroutines.experimental.delay(milliseconds.toLong())
            task()
        }
    }

    override fun onUIThread(task: () -> Unit) {
        Platform.runLater(task)
    }
}