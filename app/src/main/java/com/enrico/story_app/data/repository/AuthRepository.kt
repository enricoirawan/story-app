package com.enrico.story_app.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.enrico.story_app.data.ResultState
import com.enrico.story_app.data.remote.request.LoginRequest
import com.enrico.story_app.data.remote.request.RegisterRequest
import com.enrico.story_app.data.remote.response.ErrorResponse
import com.enrico.story_app.data.remote.response.LoginResult
import com.enrico.story_app.data.remote.retrofit.ApiService
import com.google.gson.Gson
import retrofit2.HttpException
import java.net.SocketTimeoutException

class AuthRepository constructor(
    private val apiService: ApiService,
){
    fun register(registerRequest: RegisterRequest): LiveData<ResultState<ErrorResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.register(registerRequest)
            emit(ResultState.Success(response))
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    // handle different HTTP error codes here (4xx)
                    val exception = throwable as HttpException
                    val statusCode = exception.code()
                    val errorResponse = Gson().fromJson(exception.response()?.errorBody()?.charStream(), ErrorResponse::class.java)
                    val errorMessage = "Error : " + errorResponse.message + ", " + "Status code " + statusCode
                    emit(ResultState.Error(errorMessage))
                }
                is SocketTimeoutException -> {
                    // handle timeout from Retrofit
                    val exception = throwable as SocketTimeoutException
                    val errorMessage = "Error : " + exception.message
                    emit(ResultState.Error(errorMessage))
                }
                else -> {
                    // generic error handling
                    emit(ResultState.Error("Terjadi kesalahan, silahkan coba ulang kembali"))
                }
            }
        }
    }

    fun login(loginRequest: LoginRequest): LiveData<ResultState<LoginResult>> = liveData {
        emit(ResultState.Loading)
        try {
            val loginResult = apiService.login(loginRequest).loginResult
            emit(ResultState.Success(loginResult))
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    // handle different HTTP error codes here (4xx)
                    val exception = throwable as HttpException
                    val statusCode = exception.code()
                    val errorResponse = Gson().fromJson(exception.response()?.errorBody()?.charStream(), ErrorResponse::class.java)
                    val errorMessage = "Error : " + errorResponse.message + ", " + "Status code " + statusCode
                    emit(ResultState.Error(errorMessage))
                }
                is SocketTimeoutException -> {
                    // handle timeout from Retrofit
                    val exception = throwable as SocketTimeoutException
                    val errorMessage = "Error : " + exception.message
                    emit(ResultState.Error(errorMessage))
                }
                else -> {
                    // generic error handling
                    emit(ResultState.Error("Terjadi kesalahan, silahkan coba ulang kembali"))
                }
            }
        }
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService)
            }.also { instance = it }
    }
}