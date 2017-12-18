package io.ducommun.gameOfLife.viewModel

class FakeScheduler : Scheduler {

    var threadNum = 0

    private var ticks = 0L
    private val tasks = mutableListOf<Task>()

    override fun immediately(task: () -> Unit) {
        threadNum += 1
        task()
        threadNum -= 1
    }

    override fun onUIThread(task: () -> Unit) {
        val savedThreadNum = threadNum
        threadNum = UI_THREAD
        task()
        threadNum = savedThreadNum
    }

    data class Task(val execute: () -> Unit, val scheduledTime: Long)

    override fun delay(milliseconds: Int, task: () -> Unit) {
        tasks.add(Task(execute = task, scheduledTime = ticks + milliseconds))
    }

    fun advance(milliseconds: Int) {
        ticks += milliseconds

        tasks
            .filter { it.scheduledTime <= ticks }
            .forEach { immediately { it.execute() } }

        tasks.removeAll { it.scheduledTime <= ticks }
    }
}

const val UI_THREAD = 999