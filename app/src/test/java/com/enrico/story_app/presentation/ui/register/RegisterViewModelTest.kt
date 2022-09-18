package com.enrico.story_app.presentation.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.enrico.DataDummy
import com.enrico.getOrAwaitValue
import com.enrico.story_app.data.ResultState
import com.enrico.story_app.data.remote.response.ErrorResponse
import com.enrico.story_app.data.repository.AuthRepository
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
class RegisterViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authRepository : AuthRepository
    private lateinit var registerViewModel: RegisterViewModel
    private val dummyRegisterResponse = DataDummy.generateDummyErrorResponse()
    private val dummyRegisterRequest = DataDummy.generateDummyRegisterRequest()

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(authRepository)
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
    fun `when Register Should Return Success`() = runTest{
        val expectedResult = MutableLiveData<ResultState<ErrorResponse>>()
        expectedResult.value = ResultState.Success(dummyRegisterResponse)

        `when`(authRepository.register(dummyRegisterRequest)).thenReturn(expectedResult)

        val actualResult = registerViewModel.register(dummyRegisterRequest).getOrAwaitValue()

        Mockito.verify(authRepository).register(dummyRegisterRequest)
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is ResultState.Success)
    }

    @Test
    fun `when Network Error Should Return Error`() = runTest{
        val expectedResult = MutableLiveData<ResultState<ErrorResponse>>()
        expectedResult.value = ResultState.Error("Error")

        `when`(authRepository.register(dummyRegisterRequest)).thenReturn(expectedResult)

        val actualResult = registerViewModel.register(dummyRegisterRequest).getOrAwaitValue()

        Mockito.verify(authRepository).register(dummyRegisterRequest)
        Assert.assertNotNull(actualResult)
        Assert.assertTrue(actualResult is ResultState.Error)
    }
}