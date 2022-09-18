package com.enrico.story_app.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.enrico.DataDummy
import com.enrico.MainDispatcherRule
import com.enrico.getOrAwaitValue
import com.enrico.story_app.data.FakeApiService
import com.enrico.story_app.data.database.AppDatabase
import com.enrico.story_app.data.database.StoryEntity
import com.enrico.story_app.data.remote.retrofit.ApiService
import com.enrico.story_app.presentation.adapter.StoryAdapter
import com.enrico.story_app.presentation.ui.home.StoryPagingSource
import com.enrico.story_app.presentation.ui.home.noopListUpdateCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var storyRepositoryMock: StoryRepository

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var appDatabase: AppDatabase

    private val dummyToken = DataDummy.generateDummyToken()
    private val dummyStoryResponse = DataDummy.generateDummyStoryResponse()
    private val dummyStory = DataDummy.generateDummyStoryEntity()
    private val dummyErrorResponse = DataDummy.generateDummyErrorResponse()
    private val dummyMultiPartRequest = DataDummy.generateDummyMultipartFile()
    private val dummyRequestBody = DataDummy.generateDummyRequestBody()

    @Before
    fun setUp() {
        apiService = FakeApiService()
        storyRepository = StoryRepository(apiService, appDatabase)
    }

    @Test
    fun `when getStories paging Successfully`() = runTest {
        val dummyStory = dummyStory
        val data: PagingData<StoryEntity> = StoryPagingSource.snapshot(dummyStory)
        val expected = MutableLiveData<PagingData<StoryEntity>>()
        expected.value = data

        `when`(storyRepositoryMock.getStories(dummyToken)).thenReturn(expected)

        val actualStory: PagingData<StoryEntity> = storyRepositoryMock.getStories(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory, differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0].userId, differ.snapshot()[0]?.userId)
    }

    @Test
    fun `when getStoriesForMaps Successfully`() = runTest {
        val expectedResponse = dummyStoryResponse
        val actualResponse = apiService.getStoriesForMaps(dummyToken, "1")
        assertNotNull(actualResponse)
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `when uploadStory Successfully`() = runTest {
        val expectedResponse = dummyErrorResponse
        val actualResponse = apiService.uploadImage(dummyToken, dummyMultiPartRequest, dummyRequestBody)
        assertNotNull(actualResponse)
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `when uploadStoryWithLocation Successfully`() = runTest {
        val expectedResponse = dummyErrorResponse
        val actualResponse = apiService.uploadImageWithLocation(dummyToken, dummyMultiPartRequest, dummyRequestBody, dummyRequestBody, dummyRequestBody)
        assertNotNull(actualResponse)
        assertEquals(expectedResponse, actualResponse)
    }
}