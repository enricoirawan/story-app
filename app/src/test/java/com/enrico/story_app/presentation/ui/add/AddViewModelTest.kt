package com.enrico.story_app.presentation.ui.add

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.enrico.DataDummy
import com.enrico.getOrAwaitValue
import com.enrico.story_app.data.ResultState
import com.enrico.story_app.data.remote.response.ErrorResponse
import com.enrico.story_app.data.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AddViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var addViewModel: AddViewModel
    private val dummyErrorResponse = DataDummy.generateDummyErrorResponse()
    private val dummyMultiPartRequest = DataDummy.generateDummyMultipartFile()
    private val dummyRequestBody = DataDummy.generateDummyRequestBody()
    private val dummyToken = DataDummy.generateDummyToken()

    @Before
    fun setUp() {
        addViewModel = AddViewModel(storyRepository)
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
    fun `when Upload Story Should Return Success`() = runTest {
        val expectedResult = MutableLiveData<ResultState<ErrorResponse>>()
        expectedResult.value = ResultState.Success(dummyErrorResponse)

        `when`(storyRepository.uploadStory(dummyToken, dummyMultiPartRequest, dummyRequestBody)).thenReturn(expectedResult)

        val actualResult = addViewModel.uploadStory(dummyToken, dummyMultiPartRequest, dummyRequestBody).getOrAwaitValue()

        Mockito.verify(storyRepository).uploadStory(dummyToken, dummyMultiPartRequest, dummyRequestBody)
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is ResultState.Success)
    }

    @Test
    fun `when Upload Story With Location Should Return Success`() = runTest {
        val expectedResult = MutableLiveData<ResultState<ErrorResponse>>()
        expectedResult.value = ResultState.Success(dummyErrorResponse)

        `when`(storyRepository.uploadStoryWithLocation(dummyToken, dummyMultiPartRequest, dummyRequestBody, dummyRequestBody, dummyRequestBody)).thenReturn(expectedResult)

        val actualResult = addViewModel.uploadStoryWithLocation(dummyToken, dummyMultiPartRequest, dummyRequestBody, dummyRequestBody, dummyRequestBody).getOrAwaitValue()

        Mockito.verify(storyRepository).uploadStoryWithLocation(dummyToken, dummyMultiPartRequest, dummyRequestBody, dummyRequestBody, dummyRequestBody)
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is ResultState.Success)
    }

    @Test
    fun `when Upload Story Network Error Should Return Error`() {
        val expectedResult = MutableLiveData<ResultState<ErrorResponse>>()
        expectedResult.value = ResultState.Error("Error")

        `when`(storyRepository.uploadStory(dummyToken, dummyMultiPartRequest, dummyRequestBody)).thenReturn(expectedResult)

        val actualResult = addViewModel.uploadStory(dummyToken, dummyMultiPartRequest, dummyRequestBody).getOrAwaitValue()

        Mockito.verify(storyRepository).uploadStory(dummyToken, dummyMultiPartRequest, dummyRequestBody)
        Assert.assertNotNull(actualResult)
    }

    @Test
    fun `when Upload Story With Location Network Error Should Return Error`() {
        val expectedResult = MutableLiveData<ResultState<ErrorResponse>>()
        expectedResult.value = ResultState.Error("Error")

        `when`(storyRepository.uploadStoryWithLocation(dummyToken, dummyMultiPartRequest, dummyRequestBody, dummyRequestBody, dummyRequestBody)).thenReturn(expectedResult)

        val actualResult = addViewModel.uploadStoryWithLocation(dummyToken, dummyMultiPartRequest, dummyRequestBody, dummyRequestBody, dummyRequestBody).getOrAwaitValue()

        Mockito.verify(storyRepository).uploadStoryWithLocation(dummyToken, dummyMultiPartRequest, dummyRequestBody, dummyRequestBody, dummyRequestBody)
        Assert.assertNotNull(actualResult)
    }
}
