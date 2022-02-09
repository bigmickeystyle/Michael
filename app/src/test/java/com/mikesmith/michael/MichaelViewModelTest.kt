package com.mikesmith.michael

import com.mikesmith.michael.network.MichaelService
import com.mikesmith.michael.network.WordForTheDay
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import retrofit2.Response

@ExperimentalCoroutinesApi
class MichaelViewModelTest {

    private val michaelService: MichaelService = mock {
        onBlocking { getWordForToday() } doReturn Response.success(WordForTheDay("test"))
    }

    private val testDispatcher = Dispatchers.Unconfined

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `game starts as idle`() {
        val viewModel = MichaelViewModel(michaelService)

        runTest {
            assertEquals(viewModel.gameState, MichaelState.Idle)
        }
    }

    @Test
    fun `starts game on start click with word of the day`() {
        val viewModel = MichaelViewModel(michaelService)

        runTest {
            viewModel.onStartClick()

            assertEquals(
                viewModel.gameState,
                MichaelState.Playing(
                    "TEST",
                    0,
                    listOf(
                        TileRow(
                            listOf(
                                Tile(TileState.IDLE),
                                Tile(TileState.IDLE),
                                Tile(TileState.IDLE),
                                Tile(TileState.IDLE),
                            ),
                        ),
                        TileRow(
                            listOf(
                                Tile(TileState.IDLE),
                                Tile(TileState.IDLE),
                                Tile(TileState.IDLE),
                                Tile(TileState.IDLE),
                            ),
                        ),
                        TileRow(
                            listOf(
                                Tile(TileState.IDLE),
                                Tile(TileState.IDLE),
                                Tile(TileState.IDLE),
                                Tile(TileState.IDLE),
                            ),
                        ),
                        TileRow(
                            listOf(
                                Tile(TileState.IDLE),
                                Tile(TileState.IDLE),
                                Tile(TileState.IDLE),
                                Tile(TileState.IDLE),
                            ),
                        ),
                        TileRow(
                            listOf(
                                Tile(TileState.IDLE),
                                Tile(TileState.IDLE),
                                Tile(TileState.IDLE),
                                Tile(TileState.IDLE),
                            ),
                        ),
                    )
                )
            )
        }

    }
}
