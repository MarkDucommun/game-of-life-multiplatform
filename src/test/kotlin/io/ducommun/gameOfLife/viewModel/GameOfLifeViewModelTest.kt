package io.ducommun.gameOfLife.viewModel

import io.ducommun.gameOfLife.Coordinate
import io.ducommun.gameOfLife.HashSetPlane
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GameOfLifeViewModelTest {

    val scheduler = TestScheduler()

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

        val canvas = TestView(dimension = 128, aliveColor = 101, deadColor = 100)

        viewModel(
            coordinates = setOf(Coordinate(x = 0, y = 0)),
            actions = defaultActions.copy(drawPlane = { canvas.set(it) }),
            settings = defaultSettings.copy(
                canvasDimension = canvas.width,
                aliveColor = canvas.aliveColor,
                deadColor = canvas.deadColor
            )
        )

        canvas shouldEqual {
            row(0, 0, 0, 0)
            row(0, 0, 1, 0)
            row(0, 0, 0, 0)
            row(0, 0, 0, 0)
        }
    }

    @Test
    fun sets_the_title_when_plane_is_set() {

        var titleCount = 0

        viewModel(
            coordinates = setOf(Coordinate(x = 0, y = 0)),
            actions = defaultActions.copy(
                updateStats = { stats ->

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
                },
                currentTime = { 0L }
            )
        )

        assertEquals(expected = 1, actual = titleCount)
    }

    @Test
    fun sets_the_title_when_we_zoom_or_pan() {

        listOf(
            GameOfLifeViewModel::zoomIn to "zoomIn",
            GameOfLifeViewModel::zoomOut to "zoomOut",
            GameOfLifeViewModel::panLeft to "panLeft",
            GameOfLifeViewModel::panRight to "panRight",
            GameOfLifeViewModel::panUp to "panUp",
            GameOfLifeViewModel::panDown to "panDown"
        ).forEach { (fnThatUpdatesStats, functionName) ->

            var titleCount = 0

            val subject: GameOfLifeViewModel = viewModel(actions = defaultActions.copy(updateStats = { titleCount++ }))

            fnThatUpdatesStats.invoke(subject)

            // once on set plane, once on zoom/pan action
            assertEquals(expected = 2, actual = titleCount, message = "Function $functionName responsible for failure")
        }
    }

    @Test
    fun draws_on_the_UI_thread() {

        var threadNum: Int? = null

        val scheduler = TestScheduler()

        viewModel(
            actions = defaultActions.copy(
                drawPlane = {
                    threadNum = scheduler.threadNum
                }
            )
        )

        assertEquals(UI_THREAD, threadNum)
    }

    @Test
    fun draws_diff_on_the_UI_thread() {

        var threadNum: Int? = null

        val scheduler = TestScheduler()

        viewModel(
            actions = defaultActions.copy(
                drawPlaneDiff = {
                    threadNum = scheduler.currentThread
                }
            )
        ) {
            start()

            scheduler.advance(milliseconds = 100)

            assertEquals(UI_THREAD, threadNum)
        }
    }

    @Test
    fun sets_title_on_the_UI_thread() {

        var threadNum: Int? = null

        val scheduler = TestScheduler()

        viewModel(actions = defaultActions.copy(updateStats = { threadNum = scheduler.currentThread }))

        assertEquals(UI_THREAD, threadNum)
    }

    @Test
    fun sets_the_title_and_increments_generation_when_running() {

        val scheduler = TestScheduler()

        var generation = -1

        viewModel(
            scheduler = scheduler,
            actions = defaultActions.copy(updateStats = { generation = it.generation })
        ) {

            start()

            repeat(10) { count ->

                scheduler.advance(100)

                assertEquals(expected = count + 1, actual = generation)
            }
        }
    }

    @Test
    fun tracks_elapsed_time_when_running() {

        val scheduler = TestScheduler()

        var elapsedTime = -1L

        var currentTime = 0L

        viewModel(
            actions = defaultActions.copy(
                updateStats = { elapsedTime = it.elapsedSeconds },
                currentTime = { currentTime }
            ),
            scheduler = scheduler
        ) {

            start()

            currentTime = 1000

            scheduler.advance(100)

            assertEquals(expected = 1000, actual = elapsedTime)

            stop()

            currentTime = 1500

            start()

            currentTime = 2000

            scheduler.advance(100)

            assertEquals(expected = 1500, actual = elapsedTime)
        }
    }

    @Test
    fun tracks_frames_per_second_when_running() {

        val scheduler = TestScheduler()

        var framesPerSecond: Double? = null

        var currentTime = 0L

        viewModel(
            coordinates = setOf(
                Coordinate(x = -1, y = 0),
                Coordinate(x = 0, y = 0),
                Coordinate(x = 1, y = 0)
            ),
            actions = defaultActions.copy(
                updateStats = { framesPerSecond = it.fps },
                currentTime = { currentTime }
            ),
            scheduler = scheduler
        ) {

            start()

            currentTime = 2000

            scheduler.advance(100)

            assertEquals(expected = 0.5, actual = framesPerSecond)

            currentTime = 3000

            scheduler.advance(100)

            assertEquals(expected = 1.0, actual = framesPerSecond)
        }
    }

    @Test
    fun frames_per_second_only_changes_once_per_second() {

        val scheduler = TestScheduler()

        var framesPerSecond: Double? = null

        var currentTime = 0L

        viewModel(
            coordinates = setOf(
                Coordinate(x = -1, y = 0),
                Coordinate(x = 0, y = 0),
                Coordinate(x = 1, y = 0)
            ),
            actions = defaultActions.copy(
                updateStats = { framesPerSecond = it.fps },
                currentTime = { currentTime }
            ),
            scheduler = scheduler
        ) {

            start()

            currentTime = 500

            scheduler.advance(100)

            assertEquals(expected = 0.0, actual = framesPerSecond)

            currentTime = 1000

            scheduler.advance(100)

            assertEquals(expected = 2.0, actual = framesPerSecond)
        }
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

        var setTitleCount = 0

        viewModel(
            coordinates = setOf(
                Coordinate(x = -1, y = 0),
                Coordinate(x = 0, y = 0),
                Coordinate(x = 1, y = 0)
            ),
            actions = defaultActions.copy(
                updateStats = {
                    setTitleCount++
                    assertEquals(expected = setTitleCount - 1, actual = it.generation)
                }
            )
        ) {

            next()

            next()

            assertEquals(expected = 3, actual = setTitleCount)
        }
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

        val scheduler = TestScheduler()

        val view = TestView(dimension = 2, aliveColor = 101, deadColor = 100)

        viewModel(
            coordinates = setOf(
                Coordinate(x = -1, y = 0),
                Coordinate(x = 0, y = 0),
                Coordinate(x = 1, y = 0)
            ),
            settings = defaultSettings.copy(
                boardDimension = view.width,
                canvasDimension = view.width,
                aliveColor = view.aliveColor,
                deadColor = view.deadColor
            ),
            actions = defaultActions.copy(
                drawPlane = { view.set(it) },
                drawPlaneDiff = { view.setDiff(it) }
            ),
            scheduler = scheduler
        ) {

            start()

            view shouldEqual {
                // -1  0
                row(1, 1) // 0
                row(0, 0) // -1
            }

            scheduler.advance(milliseconds = 100)

            view shouldEqual {
                row(0, 1)
                row(0, 1)
            }

            toggleRunning()

            scheduler.advance(milliseconds = 100)

            view shouldEqual {
                row(0, 1)
                row(0, 1)
            }

            scheduler.advance(milliseconds = 100)

            view shouldEqual {
                row(0, 1)
                row(0, 1)
            }
        }
    }

    data class Actions(
        val drawPlane: (IntArray) -> Unit,
        val drawPlaneDiff: (List<Rect>) -> Unit,
        val updateStats: (GameOfLifeViewModel.Stats) -> Unit,
        val currentTime: () -> Long
    )

    data class Settings(
        val canvasDimension: Int,
        val boardDimension: Int,
        val aliveColor: Int,
        val deadColor: Int
    )

    private val defaultActions = Actions(
        drawPlane = {},
        drawPlaneDiff = {},
        updateStats = {},
        currentTime = { -1L }
    )

    private val defaultSettings = Settings(
        canvasDimension = 128,
        boardDimension = 4,
        aliveColor = 101,
        deadColor = 100
    )

    private fun viewModel(
        coordinates: Set<Coordinate> = emptySet(),
        actions: Actions = defaultActions,
        settings: Settings = defaultSettings,
        scheduler: Scheduler = TestScheduler(),
        use: GameOfLifeViewModel.() -> Unit = {}
    ): GameOfLifeViewModel {

        return GameOfLifeViewModel(
            scheduler = scheduler,
            canvasWidth = settings.canvasDimension,
            canvasHeight = settings.canvasDimension,
            initialBoardWidth = 4,
            initialBoardHeight = 4,
            aliveColor = settings.aliveColor,
            deadColor = settings.deadColor,
            initialFps = 10
        ).apply {

            onDraw(actions.drawPlane)
            onDrawDiff(actions.drawPlaneDiff)
            onSetStats(actions.updateStats)
            onGetTimeMillis(actions.currentTime)

            setPlane(HashSetPlane(coordinates))

            use()
        }
    }

    class TestView(
        val width: Int,
        val height: Int,
        val deadColor: Int = 0,
        val aliveColor: Int = 1
    ) {

        constructor(dimension: Int, aliveColor: Int, deadColor: Int) : this(
            width = dimension,
            height = dimension,
            aliveColor = aliveColor,
            deadColor = deadColor
        )

        constructor(dimension: Int) : this(
            width = dimension,
            height = dimension
        )

        private var inner = IntArray(size = width * height)

        fun set(canvas: IntArray) { inner = canvas }

        fun setDiff(rects: List<Rect>) {
            for (rect in rects) {
                assertTrue("x=${rect.x} out of range") { rect.x in 0..width - rect.width }
                assertTrue("y=${rect.y} out of range") { rect.y in 0..height - rect.height }
                for (x in rect.x until rect.x + rect.width)
                    for (y in rect.y until rect.y + rect.height)
                        inner[x + width * y] = rect.color
            }
        }

        infix fun shouldEqual(createCanvas: Grid.() -> Unit) {

            val grid = Grid(width = width, height = height, aliveColor = aliveColor, deadColor = deadColor)

            grid.createCanvas()

            val expectedCanvas = grid.expectedCanvas()

            assertEquals(expected = width * height, actual = inner.size, message = "Unexpected canvas size")

            for (y in 0 until height) for (x in 0 until width) {
                val index = x + width * y
                val expected = expectedCanvas[index]
                val actual = inner[index]
                if (expected != actual) assertEquals(
                    expected = expected,
                    actual = actual,
                    message = "Mismatch at ($x, $y)"
                )
            }
        }

        fun print() { inner.forEach { println(it) } }
    }

    class Grid(val width: Int, val height: Int, val aliveColor: Int, val deadColor: Int) {

        private var rows = mutableListOf<Iterable<Int>>()

        fun row(vararg cells: Int) {
            val deadCell = IntArray(width / cells.size) { deadColor }
                .asIterable()

            val aliveCell = IntArray(width / cells.size) { aliveColor }
                .asIterable()

            rows.add(cells.flatMap { cell ->
                when (cell) {
                    0 -> deadCell
                    1 -> aliveCell
                    else -> throw Throwable("Unexpected cell value")
                }
            })
        }

        fun expectedCanvas(): IntArray {
            return rows.flatMap { row -> (1..height / rows.size).flatMap { row } }.toIntArray()
        }
    }


    private fun row(vararg cells: Int): Iterable<Int> {

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

    private fun grid(vararg rows: Iterable<Int>): IntArray =
        rows.flatMap { row -> (1..canvasHeight / rows.size).flatMap { row } }.toIntArray()

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
