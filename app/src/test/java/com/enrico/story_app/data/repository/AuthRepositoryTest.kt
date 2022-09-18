package com.enrico.story_app.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.enrico.DataDummy
import com.enrico.MainDispatcherRule
import com.enrico.story_app.data.FakeApiService
import com.enrico.story_app.data.remote.retrofit.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var apiService: ApiService
    private lateinit var authRepository: AuthRepository
    private val dummyRegisterResponse = DataDummy.generateDummyErrorResponse()
    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()
    private val dummyRegisterRequest = DataDummy.generateDummyRegisterRequest()
    private val dummyLoginRequest = DataDummy.generateDummyLoginRequest()

    @Before
    fun setUp() {
        apiService = FakeApiService()
        authRepository = AuthRepository(apiService)
    }

    @Test
    fun `when register Successfully`() = runTest {
        val expectedResponse = dummyRegisterResponse
        val actualResponse = apiService.register(dummyRegisterRequest)
        assertNotNull(actualResponse)
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `when Login Successfully`() = runTest {
        val expectedResponse = dummyRegisterResponse
        val actualResponse = apiService.register(dummyRegisterRequest)
        assertNotNull(actualResponse)
        assertEquals(expectedResponse, actualResponse)
    }
}