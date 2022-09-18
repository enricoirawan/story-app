package com.enrico.story_app.presentation.ui.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.enrico.DataDummy
import com.enrico.getOrAwaitValue
import com.enrico.story_app.data.ResultState
import com.enrico.story_app.data.remote.response.Story
import com.enrico.story_app.data.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var mapsViewModel: MapsViewModel
    private val dummyStoryResponse = DataDummy.generateDummyStoryResponse()
    private val dummyToken = DataDummy.generateDummyToken()

    @Before
    fun setUp() {
        mapsViewModel = MapsViewModel(storyRepository)
    }

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setupDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when Get Stories For Maps Should Return Success`() = runTest {
        val expectedResult = MutableLiveData<ResultState<List<Story>>>()
        expectedResult.value = ResultState.Success(dummyStoryResponse.listStory)

        Mockito.`when`(storyRepository.getStoriesForMaps(dummyToken)).thenReturn(expectedResult)

        val actualResult = mapsViewModel.getStoriesForMaps(dummyToken).getOrAwaitValue()

        Mockito.verify(storyRepository).getStoriesForMaps(dummyToken)
        assertNotNull(actualResult)
        assertTrue(actualResult is ResultState.Success)
    }

    @Test
    fun `when Network Error Should Return Error`() = runTest {
        val expectedResult = MutableLiveData<ResultState<List<Story>>>()
        expectedResult.value = ResultState.Error("Error")

        Mockito.`when`(storyRepository.getStoriesForMaps(dummyToken)).thenReturn(expectedResult)

        val actualResult = mapsViewModel.getStoriesForMaps(dummyToken).getOrAwaitValue()

        Mockito.verify(storyRepository).getStoriesForMaps(dummyToken)
        assertNotNull(actualResult)
        assertTrue(actualResult is ResultState.Error)
    }
}