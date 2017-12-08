package io.ducommun.gameOfLife.viewModel

import io.ducommun.gameOfLife.Coordinate
import io.ducommun.gameOfLife.HashSetPlane
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

const val UI_THREAD = 999

class FakeScheduler : Scheduler {
    var threadNum = 0

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

    private var ticks = 0L

    data class Task(val execute: () -> Unit, val scheduledTime: Long)

    private val tasks = mutableListOf<Task>()

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

class GameOfLifeViewModelTest {

    val scheduler = FakeScheduler()

    val deadColor = 100
    val aliveColor = 101
    val canvasWidth = 128
    val canvasHeight = 128

    val subject = GameOfLifeViewModel(
        scheduler = scheduler,
        canvasWidth = canvasWidth,
        canvasHeight = canvasHeight,
        initialBoardWidth = 4,
        initialBoardHeight = 4,
        aliveColor = aliveColor,
        deadColor = deadColor,
        initialFps = 10
    )

    var canvas = IntArray(canvasWidth * canvasHeight)

    @BeforeTest
    fun setUp() {
        subject.onDraw {
            assertEquals(canvasWidth * canvasHeight, it.size, "Unexpected canvas size")
            canvas = it
        }
        subject.onDrawDiff { rects ->
            for (rect in rects) {
                assertTrue("x=${rect.x} out of range") { rect.x in 0 .. canvasWidth - rect.width }
                assertTrue("y=${rect.y} out of range") { rect.y in 0 .. canvasHeight - rect.height }
                for (x in rect.x until rect.x + rect.width)
                    for (y in rect.y until rect.y + rect.height)
                        canvas[x + canvasWidth * y] = rect.color
            }
        }
    }

    @Test
    fun draws_the_starting_plane() {
        val emptyRow = IntArray(128) { deadColor }
        val paintedRow = IntArray(64) { deadColor } +
                         IntArray(32) { aliveColor } +
                         IntArray(32) { deadColor }

        val expectedCanvas = (0..127).flatMap {
            if (it in 32..63) paintedRow.asIterable() else emptyRow.asIterable()
        }.toIntArray()

        subject.setPlane(HashSetPlane(setOf(Coordinate(x = 0, y = 0))))

        assertCanvasEqual(expectedCanvas)
    }

    @Test
    fun draws_on_the_UI_thread() {
        var threadNum: Int? = null

        subject.onDraw { threadNum = scheduler.threadNum }

        subject.setPlane(HashSetPlane(setOf(Coordinate(x = 0, y = 0))))

        assertEquals(UI_THREAD, threadNum)
    }

    @Test
    fun calling_the_start_method_and_advancing_time_by_more_than_frame_rate_draws_next_plane() {
        subject.setPlane(HashSetPlane(setOf(
            Coordinate(x = -1, y = 0),
            Coordinate(x = 0, y = 0),
            Coordinate(x = 1, y = 0)
        )))

        subject.start()

        (1..1).forEach { iteration ->

            scheduler.advance(milliseconds = 99)

            var expectedCanvas = {
                val emptyRow = IntArray(128) { deadColor }
                val paintedRow = IntArray(32) { deadColor } +
                                 IntArray(96) { aliveColor }

                (0..127).flatMap {
                    if (it in 32..63) paintedRow.asIterable() else emptyRow.asIterable()
                }.toIntArray()
            }.invoke()

            assertCanvasEqual(expectedCanvas, "iteration ${2 * iteration - 1}")

            scheduler.advance(milliseconds = 1)

            expectedCanvas = {
                val emptyRow = IntArray(128) { deadColor }
                val paintedRow = IntArray(64) { deadColor } +
                                 IntArray(32) { aliveColor } +
                                 IntArray(32) { deadColor }

                (0..127).flatMap {
                    if (it in 0..95) paintedRow.asIterable() else emptyRow.asIterable()
                }.toIntArray()
            }.invoke()

            assertCanvasEqual(expectedCanvas, "iteration ${2 * iteration}")

            scheduler.advance(milliseconds = 100)
        }
    }

    @Test
    fun draws_diff_on_the_UI_thread() {

        var threadNum: Int? = null

        subject.onDrawDiff { threadNum = scheduler.threadNum }

        subject.setPlane(HashSetPlane(setOf(Coordinate(x = 0, y = 0))))

        subject.start()

        scheduler.advance(milliseconds = 100)

        assertEquals(UI_THREAD, threadNum)
    }

    @Test
    fun cells_out_of_bounds_are_not_drawn_when_setting_plane() {

        subject.setPlane(HashSetPlane(setOf(
            Coordinate(x = 5, y = 5)
        )))

        assertCanvasEqual(IntArray(canvasWidth * canvasHeight) { deadColor })
    }

    @Test
    fun cells_out_of_bounds_are_not_drawn_when_running() {
        subject.setPlane(HashSetPlane(setOf(
            Coordinate(x = -2, y = -3),
            Coordinate(x = -3, y = -3),
            Coordinate(x = -4, y = -3)
        )))
        subject.start()
        scheduler.advance(milliseconds = 1000)

        assertCanvasEqual(IntArray(canvasWidth * canvasHeight) { deadColor })
    }

    private fun assertCanvasEqual(expectedCanvas: IntArray, message: String? = null) {
        for (y in 0..127) for (x in 0..127) {
            val index = x + 128 * y
            val expected = expectedCanvas[index]
            val actual = canvas[index]
            if (expected != actual) assertEquals(
                expected = expected,
                actual = actual,
                message = "Mismatch at ($x, $y)" + if (message == null) "" else ", $message"
            )
        }
    }
}
