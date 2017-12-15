package io.ducommun.gameOfLife.viewModel

import io.ducommun.gameOfLife.Coordinate
import io.ducommun.gameOfLife.HashSetPlane
import kotlin.jvm.javaClass
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
    var canvasWidth = 128
    var canvasHeight = 128

    var subject = GameOfLifeViewModel(
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

    fun row(vararg cells: Int): Iterable<Int> {
        val deadCell = IntArray(canvasWidth / cells.size) { deadColor }
            .asIterable()
        val aliveCell = IntArray(canvasWidth / cells.size) { aliveColor }
            .asIterable()

        return cells.flatMap { cell ->
            when (cell) {
                0 -> deadCell
                1 -> aliveCell
                else -> throw Throwable("Unexpected cell value")
            }
        }
    }

    fun grid(vararg rows: Iterable<Int>): IntArray =
        rows.flatMap { row -> (1..canvasHeight / rows.size).flatMap { row } }.toIntArray()

    private fun setDimensions(
        boardWidth: Int = 4,
        boardHeight: Int = 4,
        canvasWidth: Int = 128,
        canvasHeight: Int = 128
    ) {

        this.canvasWidth = canvasWidth
        this.canvasHeight = canvasHeight

        canvas = IntArray(canvasWidth * canvasHeight)

        subject = GameOfLifeViewModel(
            scheduler = scheduler,
            canvasWidth = canvasWidth,
            canvasHeight = canvasHeight,
            initialBoardWidth = boardWidth,
            initialBoardHeight = boardHeight,
            aliveColor = aliveColor,
            deadColor = deadColor,
            initialFps = 10
        ).apply {
            onDraw {
                assertEquals(canvasWidth * canvasHeight, it.size, "Unexpected canvas size")
                canvas = it
            }
            onDrawDiff { rects ->
                for (rect in rects) {
                    assertTrue("x=${rect.x} out of range") { rect.x in 0..canvasWidth - rect.width }
                    assertTrue("y=${rect.y} out of range") { rect.y in 0..canvasHeight - rect.height }
                    for (x in rect.x until rect.x + rect.width)
                        for (y in rect.y until rect.y + rect.height)
                            canvas[x + canvasWidth * y] = rect.color
                }
            }
        }
    }

    @BeforeTest
    fun setUp() {
        setDimensions()
    }

    @Test
    fun draws_the_starting_plane() {

        subject.setPlane(HashSetPlane(setOf(Coordinate(x = 0, y = 0))))

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 0, 1, 0),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )
    }

    @Test
    fun sets_the_title_when_plane_is_set() {
        var titleCount = 0

        subject.onSetStats { stats ->

            assertEquals(
                expected = GameOfLifeViewModel.Stats(
                    running = false,
                    fps = 0.0,
                    origin = Coordinate(0, 0),
                    xDimension = 4,
                    yDimension = 4,
                    generation = 0,
                    cellCount = 1,
                    elapsedSeconds = 0
                ),
                actual = stats
            )

            titleCount++
        }

        subject.setPlane(HashSetPlane(setOf(Coordinate(x = 0, y = 0))))

        assertEquals(expected = 1, actual = titleCount)

    }

    @Test
    fun sets_the_title_when_we_zoom_in() {

        listOf(
            subject::zoomIn to "zoomIn",
            subject::zoomOut to "zoomOut",
            subject::panLeft to "panLeft",
            subject::panRight to "panRight",
            subject::panUp to "panUp",
            subject::panDown to "panDown"
        ).forEach { (fnThatUpdatesStats, functionName) ->

            subject.setPlane(HashSetPlane(setOf(Coordinate(x = 0, y = 0))))

            var titleCount = 0

            subject.onSetStats { stats -> titleCount++ }

            fnThatUpdatesStats.invoke()

            assertEquals(expected = 1, actual = titleCount, message = "Function $functionName responsible for failure")
        }
    }

    @Test
    fun draws_on_the_UI_thread() {
        var threadNum: Int? = null

        subject.onDraw { threadNum = scheduler.threadNum }

        subject.setPlane(HashSetPlane(setOf(Coordinate(x = 0, y = 0))))

        assertEquals(UI_THREAD, threadNum)
    }

    @Test
    fun sets_title_on_the_UI_thread() {
        var threadNum: Int? = null

        subject.onSetStats { threadNum = scheduler.threadNum }

        subject.setPlane(HashSetPlane(setOf(Coordinate(x = 0, y = 0))))

        assertEquals(UI_THREAD, threadNum)
    }

    @Test
    fun sets_the_title_and_increments_generation_when_running() {

        subject.setPlane(
            HashSetPlane(
                setOf(

                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 1, y = 0)
                )
            )
        )

        var setTitleCount = 0

        subject.onSetStats {
            setTitleCount++
            assertEquals(expected = setTitleCount, actual = it.generation)
        }

        var currentTime = 0L

        subject.onGetTimeMillis { currentTime }

        subject.start()

        repeat(10) { scheduler.advance(100) }

        assertEquals(expected = 10, actual = setTitleCount)

    }

    @Test
    fun tracks_elapsed_time_when_running() {

        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 1, y = 0)
                )
            )
        )

        var elapsedTime: Long? = null

        subject.onSetStats {
            elapsedTime = it.elapsedSeconds
        }

        var currentTime = 0L

        subject.onGetTimeMillis { currentTime }

        subject.start()

        currentTime = 1000

        scheduler.advance(100)

        assertEquals(expected = 1000, actual = elapsedTime)

        subject.stop()

        currentTime = 1500

        subject.start()

        currentTime = 2000

        scheduler.advance(100)

        assertEquals(expected = 1500, actual = elapsedTime)
    }

    @Test
    fun tracks_frames_per_second_when_running() {

        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 1, y = 0)
                )
            )
        )

        var framesPerSecond: Double? = null

        subject.onSetStats {
            framesPerSecond = it.fps
        }

        var currentTime = 0L

        subject.onGetTimeMillis { currentTime }

        subject.start()

        currentTime = 2000

        scheduler.advance(100)

        assertEquals(expected = 0.5, actual = framesPerSecond)

        currentTime = 3000

        scheduler.advance(100)

        assertEquals(expected = 1.0, actual = framesPerSecond)
    }


    @Test
    fun frames_per_second_only_changes_once_per_second() {

        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 1, y = 0)
                )
            )
        )

        var framesPerSecond: Double? = null

        subject.onSetStats {
            framesPerSecond = it.fps
        }

        var currentTime = 0L

        subject.onGetTimeMillis { currentTime }

        subject.start()

        currentTime = 500

        scheduler.advance(100)

        assertEquals(expected = 0.0, actual = framesPerSecond)

        currentTime = 1000

        scheduler.advance(100)

        assertEquals(expected = 2.0, actual = framesPerSecond)
    }

    @Test
    fun calling_the_start_method_and_advancing_time_by_more_than_frame_rate_draws_next_plane() {
        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 1, y = 0)
                )
            )
        )

        subject.start()

        (1..1).forEach { iteration ->

            scheduler.advance(milliseconds = 99)

            assertCanvasEqual(
                grid(
                    row(0, 0, 0, 0),
                    row(0, 1, 1, 1),
                    row(0, 0, 0, 0),
                    row(0, 0, 0, 0)
                ), message = "iteration ${2 * iteration - 1}"
            )

            scheduler.advance(milliseconds = 1)

            assertCanvasEqual(
                grid(
                    row(0, 0, 1, 0),
                    row(0, 0, 1, 0),
                    row(0, 0, 1, 0),
                    row(0, 0, 0, 0)
                ), message = "iteration ${2 * iteration}"
            )

            scheduler.advance(milliseconds = 100)
        }
    }


    @Test
    fun calling_start_twice_does_not_start_two_loops() {
        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 1, y = 0)
                )
            )
        )

        subject.start()

        subject.start()

        (1..10).forEach { iteration ->

            scheduler.advance(milliseconds = 99)

            assertCanvasEqual(
                grid(
                    row(0, 0, 0, 0),
                    row(0, 1, 1, 1),
                    row(0, 0, 0, 0),
                    row(0, 0, 0, 0)
                ), message = "iteration ${2 * iteration - 1}"
            )

            scheduler.advance(milliseconds = 1)

            assertCanvasEqual(
                grid(
                    row(0, 0, 1, 0),
                    row(0, 0, 1, 0),
                    row(0, 0, 1, 0),
                    row(0, 0, 0, 0)
                ), message = "iteration ${2 * iteration}"
            )

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

        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = 5, y = 5)
                )
            )
        )

        assertCanvasEqual(IntArray(canvasWidth * canvasHeight) { deadColor })
    }

    @Test
    fun cells_out_of_bounds_are_not_drawn_when_running() {
        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -2, y = -3),
                    Coordinate(x = -3, y = -3),
                    Coordinate(x = -4, y = -3)
                )
            )
        )
        subject.start()
        scheduler.advance(milliseconds = 1000)

        assertCanvasEqual(IntArray(canvasWidth * canvasHeight) { deadColor })
    }

    @Test
    fun zoom_in_works_when_not_running() {
        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 1, y = 0)
                )
            )
        )

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 1, 1, 1),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )

        subject.zoomIn()

        assertCanvasEqual(
            grid(
                row(1, 1),
                row(0, 0)
            )
        )
    }

    @Test
    fun zoom_in_increases_cell_size_by_2_X_on_next_iteration() {
        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 1, y = 0)
                )
            )
        )

        subject.start()
        subject.zoomIn()

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 1, 1, 1),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )

        scheduler.advance(milliseconds = 100)

        assertCanvasEqual(
            grid(
                row(0, 1),
                row(0, 1)
            )
        )

        scheduler.advance(milliseconds = 100)

        assertCanvasEqual(
            grid(
                row(1, 1),
                row(0, 0)
            )
        )
    }

    @Test
    fun zoom_in_allows_for_a_minimum_board_size_of_one() {

        setDimensions(boardWidth = 2, boardHeight = 2)

        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = 2, y = 0),
                    Coordinate(x = 1, y = 0),
                    Coordinate(x = 0, y = 0)
                )
            )
        )

        subject.start()
        subject.zoomIn()

        assertCanvasEqual(
            grid(
                row(0, 1),
                row(0, 0)
            )
        )

        scheduler.advance(milliseconds = 100)

        assertCanvasEqual(grid(row(0)))

        scheduler.advance(milliseconds = 100)

        assertCanvasEqual(grid(row(1)))

        subject.zoomIn()

        scheduler.advance(milliseconds = 100)

        assertCanvasEqual(grid(row(0)))
    }

    @Test
    fun zoom_out_decreases_cell_size_by_2_X_on_next_iteration() {
        setDimensions(boardWidth = 2, boardHeight = 2)

        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 1, y = 0)
                )
            )
        )

        subject.start()

        subject.zoomOut()

        assertCanvasEqual(
            grid(
                row(1, 1),
                row(0, 0)
            )
        )

        scheduler.advance(milliseconds = 100)

        assertCanvasEqual(
            grid(
                row(0, 0, 1, 0),
                row(0, 0, 1, 0),
                row(0, 0, 1, 0),
                row(0, 0, 0, 0)
            )
        )

        scheduler.advance(milliseconds = 100)

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 1, 1, 1),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )
    }

    @Test
    fun zoom_out_works_when_not_running() {
        setDimensions(boardWidth = 2, boardHeight = 2)

        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 1, y = 0)
                )
            )
        )

        assertCanvasEqual(
            grid(
                row(1, 1),
                row(0, 0)
            )
        )

        subject.zoomOut()

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 1, 1, 1),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )
    }

    @Test
    fun pan_left_moves_one_place_to_the_right_on_next_iteration() {
        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -2, y = 0),
                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0)
                )
            )
        )

        subject.start()

        subject.panLeft()

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(1, 1, 1, 0),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )

        scheduler.advance(milliseconds = 100)

        assertCanvasEqual(
            grid(
                row(0, 0, 1, 0),
                row(0, 0, 1, 0),
                row(0, 0, 1, 0),
                row(0, 0, 0, 0)
            )
        )

        scheduler.advance(milliseconds = 100)

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 1, 1, 1),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )
    }

    @Test
    fun pan_right_moves_one_left_on_next_iteration() {
        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 1, y = 0)
                )
            )
        )

        subject.start()

        subject.panRight()

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 1, 1, 1),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )

        scheduler.advance(milliseconds = 100)

        assertCanvasEqual(
            grid(
                row(0, 1, 0, 0),
                row(0, 1, 0, 0),
                row(0, 1, 0, 0),
                row(0, 0, 0, 0)
            )
        )

        scheduler.advance(milliseconds = 100)

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(1, 1, 1, 0),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )
    }

    @Test
    fun pan_up_moves_one_down_on_next_iteration() {
        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 1, y = 0)
                )
            )
        )

        subject.start()

        subject.panUp()

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 1, 1, 1),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )

        scheduler.advance(milliseconds = 100)

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 0, 1, 0),
                row(0, 0, 1, 0),
                row(0, 0, 1, 0)
            )
        )

        scheduler.advance(milliseconds = 100)

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 0, 0, 0),
                row(0, 1, 1, 1),
                row(0, 0, 0, 0)
            )
        )
    }

    @Test
    fun pan_down_moves_one_up_on_next_iteration() {
        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -1, y = -1),
                    Coordinate(x = 0, y = -1),
                    Coordinate(x = 1, y = -1)
                )
            )
        )

        subject.start()

        subject.panDown()

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 0, 0, 0),
                row(0, 1, 1, 1),
                row(0, 0, 0, 0)
            )
        )

        scheduler.advance(milliseconds = 100)

        assertCanvasEqual(
            grid(
                row(0, 0, 1, 0),
                row(0, 0, 1, 0),
                row(0, 0, 1, 0),
                row(0, 0, 0, 0)
            )
        )

        scheduler.advance(milliseconds = 100)

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 1, 1, 1),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )
    }

    @Test
    fun pan_left_works_when_not_running() {
        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -2, y = 0),
                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0)
                )
            )
        )

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(1, 1, 1, 0),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )

        subject.panLeft()

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 1, 1, 1),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )
    }

    @Test
    fun pan_right_works_when_not_running() {
        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 1, y = 0)
                )
            )
        )

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 1, 1, 1),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )

        subject.panRight()

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(1, 1, 1, 0),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )
    }

    @Test
    fun pan_up_works_when_not_running() {
        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 1, y = 0)
                )
            )
        )

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 1, 1, 1),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )

        subject.panUp()

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 0, 0, 0),
                row(0, 1, 1, 1),
                row(0, 0, 0, 0)
            )
        )
    }

    @Test
    fun pan_down_works_when_not_running() {
        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -1, y = -1),
                    Coordinate(x = 0, y = -1),
                    Coordinate(x = 1, y = -1)
                )
            )
        )

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 0, 0, 0),
                row(0, 1, 1, 1),
                row(0, 0, 0, 0)
            )
        )

        subject.panDown()

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 1, 1, 1),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )
    }

    @Test
    fun when_board_dimension_is_larger_than_canvas_dimension_it_draws_pixels_where_there_are_any_living_cells() {

        setDimensions(canvasWidth = 2, canvasHeight = 2)

        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = 1, y = 1),
                    Coordinate(x = 1, y = 0),
                    Coordinate(x = 0, y = 1),
                    Coordinate(x = 0, y = 0)
                )
            )
        )

        assertCanvasEqual(
            grid(
                row(0, 1),
                row(0, 0)
            )
        )
    }

    @Test
    fun when_board_dimension_is_larger_than_canvas_dimension_it_updates() {
        setDimensions(canvasWidth = 2, canvasHeight = 2)

        subject.run {
            setPlane(
                HashSetPlane(
                    setOf(
                        Coordinate(x = -1, y = 0),
                        Coordinate(x = 0, y = 0),
                        Coordinate(x = 1, y = 0)
                    )
                )
            )

            start()

            assertCanvasEqual(
                grid(
                    row(1, 1),
                    row(0, 0)
                )
            )

            scheduler.advance(milliseconds = 100)

            assertCanvasEqual(
                grid(
                    row(0, 1),
                    row(0, 1)
                )
            )
        }
    }

    @Test
    fun next_moves_plane_to_the_next_step_then_stops() {
        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 1, y = 0)
                )
            )
        )

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 1, 1, 1),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )

        subject.next()

        assertCanvasEqual(
            grid(
                row(0, 0, 1, 0),
                row(0, 0, 1, 0),
                row(0, 0, 1, 0),
                row(0, 0, 0, 0)
            )
        )

        subject.next()

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 1, 1, 1),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )
    }

    @Test
    fun next_sets_the_title_and_increments_the_generation_count() {

        subject.setPlane(
            HashSetPlane(
                setOf(

                    Coordinate(x = -1, y = 0),
                    Coordinate(x = 0, y = 0),
                    Coordinate(x = 1, y = 0)
                )
            )
        )

        var setTitleCount = 0

        subject.onSetStats {
            setTitleCount++
            assertEquals(expected = setTitleCount, actual = it.generation)
        }

        subject.next()

        subject.next()

        assertEquals(expected = 2, actual = setTitleCount)
    }

    @Test
    fun toggle_translates_the_canvas_coordinates_to_board_coordinates() {
        setDimensions(
            boardHeight = 4,
            boardWidth = 4,
            canvasHeight = 8,
            canvasWidth = 8
        )

        subject.setPlane(
            HashSetPlane(
                setOf(
                    Coordinate(x = 0, y = 0)
                )
            )
        )

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 0, 1, 0),
                row(0, 0, 0, 0),
                row(0, 0, 0, 0)
            )
        )

        subject.toggle(canvasX = 5.0, canvasY = 2.0)
        subject.toggle(canvasX = 3.0, canvasY = 4.0)

        assertCanvasEqual(
            grid(
                row(0, 0, 0, 0),
                row(0, 0, 0, 0),
                row(0, 1, 0, 0),
                row(0, 0, 0, 0)
            )
        )
    }

    @Test
    fun stop_stops_rendering_the_game() {
        setDimensions(canvasWidth = 2, canvasHeight = 2)

        subject.run {
            setPlane(
                HashSetPlane(
                    setOf(
                        Coordinate(x = -1, y = 0),
                        Coordinate(x = 0, y = 0),
                        Coordinate(x = 1, y = 0)
                    )
                )
            )

            start()

            assertCanvasEqual(
                grid(
                    row(1, 1),
                    row(0, 0)
                )
            )

            scheduler.advance(milliseconds = 100)

            assertCanvasEqual(
                grid(
                    row(0, 1),
                    row(0, 1)
                )
            )

            stop()

            scheduler.advance(milliseconds = 100)

            assertCanvasEqual(
                grid(
                    row(0, 1),
                    row(0, 1)
                )
            )

            scheduler.advance(milliseconds = 100)

            assertCanvasEqual(
                grid(
                    row(0, 1),
                    row(0, 1)
                )
            )
        }
    }

    @Test
    fun slow_halves_the_frames_per_second() {

        setDimensions(boardWidth = 2, boardHeight = 2)

        subject.run {
            setPlane(
                HashSetPlane(
                    setOf(
                        Coordinate(x = -1, y = 0),
                        Coordinate(x = 0, y = 0),
                        Coordinate(x = 1, y = 0)
                    )
                )
            )

            slowDown()

            start()

            assertCanvasEqual(
                grid(
                    row(1, 1),
                    row(0, 0)
                )
            )

            scheduler.advance(milliseconds = 100)

            assertCanvasEqual(
                grid(
                    row(1, 1),
                    row(0, 0)
                )
            )

            scheduler.advance(milliseconds = 100)

            assertCanvasEqual(
                grid(
                    row(0, 1),
                    row(0, 1)
                )
            )

            scheduler.advance(milliseconds = 200)

            assertCanvasEqual(
                grid(
                    row(1, 1),
                    row(0, 0)
                )
            )
        }
    }


    @Test
    fun slow_never_goes_lower_than_1_fps() {

        setDimensions(boardWidth = 2, boardHeight = 2)

        subject.run {
            setPlane(
                HashSetPlane(
                    setOf(
                        Coordinate(x = -1, y = 0),
                        Coordinate(x = 0, y = 0),
                        Coordinate(x = 1, y = 0)
                    )
                )
            )

            repeat(20) { slowDown() }

            start()

            assertCanvasEqual(
                grid(
                    row(1, 1),
                    row(0, 0)
                )
            )

            scheduler.advance(milliseconds = 999)

            assertCanvasEqual(
                grid(
                    row(1, 1),
                    row(0, 0)
                )
            )

            scheduler.advance(milliseconds = 1)

            assertCanvasEqual(
                grid(
                    row(0, 1),
                    row(0, 1)
                )
            )

            scheduler.advance(milliseconds = 1000)

            assertCanvasEqual(
                grid(
                    row(1, 1),
                    row(0, 0)
                )
            )
        }
    }

    @Test
    fun speed_up_doubles_the_frames_per_second() {

        setDimensions(boardWidth = 2, boardHeight = 2)

        subject.run {
            setPlane(
                HashSetPlane(
                    setOf(
                        Coordinate(x = -1, y = 0),
                        Coordinate(x = 0, y = 0),
                        Coordinate(x = 1, y = 0)
                    )
                )
            )

            speedUp()

            start()

            assertCanvasEqual(
                grid(
                    row(1, 1),
                    row(0, 0)
                )
            )

            scheduler.advance(milliseconds = 50)

            assertCanvasEqual(
                grid(
                    row(0, 1),
                    row(0, 1)
                )
            )

            scheduler.advance(milliseconds = 50)

            assertCanvasEqual(
                grid(
                    row(1, 1),
                    row(0, 0)
                )
            )

            scheduler.advance(milliseconds = 50)

            assertCanvasEqual(
                grid(
                    row(0, 1),
                    row(0, 1)
                )
            )
        }
    }


    @Test
    fun speed_up_never_goes_faster_than_125_fps() {

        setDimensions(boardWidth = 2, boardHeight = 2)

        subject.run {
            setPlane(
                HashSetPlane(
                    setOf(
                        Coordinate(x = -1, y = 0),
                        Coordinate(x = 0, y = 0),
                        Coordinate(x = 1, y = 0)
                    )
                )
            )

            repeat(20) { speedUp() }

            start()

            assertCanvasEqual(
                grid(
                    row(1, 1),
                    row(0, 0)
                )
            )

            scheduler.advance(milliseconds = 8)

            assertCanvasEqual(
                grid(
                    row(0, 1),
                    row(0, 1)
                )
            )

            scheduler.advance(milliseconds = 8)

            assertCanvasEqual(
                grid(
                    row(1, 1),
                    row(0, 0)
                )
            )

            scheduler.advance(milliseconds = 8)

            assertCanvasEqual(
                grid(
                    row(0, 1),
                    row(0, 1)
                )
            )
        }
    }

    @Test
    fun toggle_running_starts_if_stopped() {
        setDimensions(boardWidth = 2, boardHeight = 2)

        subject.run {
            setPlane(
                HashSetPlane(
                    setOf(
                        Coordinate(x = -1, y = 0),
                        Coordinate(x = 0, y = 0),
                        Coordinate(x = 1, y = 0)
                    )
                )
            )

            toggleRunning()

            assertCanvasEqual(
                grid(
                    row(1, 1),
                    row(0, 0)
                )
            )

            scheduler.advance(milliseconds = 100)

            assertCanvasEqual(
                grid(
                    row(0, 1),
                    row(0, 1)
                )
            )

            scheduler.advance(milliseconds = 100)

            assertCanvasEqual(
                grid(
                    row(1, 1),
                    row(0, 0)
                )
            )

            scheduler.advance(milliseconds = 100)

            assertCanvasEqual(
                grid(
                    row(0, 1),
                    row(0, 1)
                )
            )
        }
    }

    @Test
    fun toggle_running_stops_if_started() {
        setDimensions(boardWidth = 2, boardHeight = 2)

        subject.run {
            setPlane(
                HashSetPlane(
                    setOf(
                        Coordinate(x = -1, y = 0),
                        Coordinate(x = 0, y = 0),
                        Coordinate(x = 1, y = 0)
                    )
                )
            )

            start()

            assertCanvasEqual(
                grid(
                    row(1, 1),
                    row(0, 0)
                )
            )

            scheduler.advance(milliseconds = 100)

            assertCanvasEqual(
                grid(
                    row(0, 1),
                    row(0, 1)
                )
            )

            toggleRunning()

            scheduler.advance(milliseconds = 100)

            assertCanvasEqual(
                grid(
                    row(0, 1),
                    row(0, 1)
                )
            )

            scheduler.advance(milliseconds = 100)

            assertCanvasEqual(
                grid(
                    row(0, 1),
                    row(0, 1)
                )
            )
        }
    }

    private fun assertCanvasEqual(expectedCanvas: IntArray, message: String? = null) {
        for (y in 0 until canvasHeight) for (x in 0 until canvasWidth) {
            val index = x + canvasWidth * y
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
