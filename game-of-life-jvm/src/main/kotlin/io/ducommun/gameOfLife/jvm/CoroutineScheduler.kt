package io.ducommun.gameOfLife.jvm

import io.ducommun.gameOfLife.viewModel.Scheduler
import javafx.application.Platform
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

object CoroutineScheduler : Scheduler {

    override fun immediately(task: () -> Unit) {
        launch { task() }
    }

    override fun delay(milliseconds: Int, task: () -> Unit) {
        launch {
            delay(milliseconds.toLong())
            task()
        }
    }

    override fun onUIThread(task: () -> Unit) {
        Platform.runLater(task)
    }
}