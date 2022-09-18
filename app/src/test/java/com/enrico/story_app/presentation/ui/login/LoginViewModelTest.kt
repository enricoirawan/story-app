package com.enrico.story_app.presentation.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.enrico.DataDummy
import com.enrico.getOrAwaitValue
import com.enrico.story_app.data.ResultState
import com.enrico.story_app.data.repository.AuthRepository
import com.enrico.story_app.data.remote.response.LoginResult
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
class LoginViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authRepository : AuthRepository
    private lateinit var loginViewModel: LoginViewModel
    private val dummyLoginResponse = DataDummy.generateDummyLoginResponseResult()
    private val dummyLoginRequest = DataDummy.generateDummyLoginRequest()

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(authRepository)
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
    fun `when Login Should Return Success`() = runTest {
        val expectedResult = MutableLiveData<ResultState<LoginResult>>()
        expectedResult.value = ResultState.Success(dummyLoginResponse)

        Mockito.`when`(authRepository.login(dummyLoginRequest)).thenReturn(expectedResult)

        val actualResult = loginViewModel.login(dummyLoginRequest).getOrAwaitValue()

        Mockito.verify(authRepository).login(dummyLoginRequest)
        assertNotNull(actualResult)
        assertTrue(actualResult is ResultState.Success)
    }

    @Test
    fun `when Network Error Should Return Error`() = runTest {
        val expectedResult = MutableLiveData<ResultState<LoginResult>>()
        expectedResult.value = ResultState.Error("Error")

        Mockito.`when`(authRepository.login(dummyLoginRequest)).thenReturn(expectedResult)

        val actualResult = loginViewModel.login(dummyLoginRequest).getOrAwaitValue()

        Mockito.verify(authRepository).login(dummyLoginRequest)
        assertNotNull(actualResult)
        assertTrue(actualResult is ResultState.Error)
    }
}