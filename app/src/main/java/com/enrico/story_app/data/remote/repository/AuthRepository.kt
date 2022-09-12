package com.enrico.story_app.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.enrico.story_app.data.ResultState
import com.enrico.story_app.data.remote.request.LoginRequest
import com.enrico.story_app.data.remote.request.RegisterRequest
import com.enrico.story_app.data.remote.response.ErrorResponse
import com.enrico.story_app.data.remote.response.LoginResponse
import com.enrico.story_app.data.remote.response.LoginResult
import com.enrico.story_app.data.remote.retrofit.ApiService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository private constructor(
    private val apiService: ApiService,
){
    private val loginResult = MediatorLiveData<ResultState<LoginResult>>()
    private val registerResult = MediatorLiveData<ResultState<ErrorResponse>>()

    fun register(registerRequest: RegisterRequest): LiveData<ResultState<ErrorResponse>> {
        registerResult.value = ResultState.Loading
        val client = apiService.register(registerRequest)

        client.enqueue(object : Callback<ErrorResponse> {
            override fun onResponse(
                call: Call<ErrorResponse>,
                response: Response<ErrorResponse>
            ) {
                if(response.isSuccessful){
                    val response = response.body()!!
                    registerResult.value = ResultState.Success(response)
                }else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java)
                    registerResult.value = ResultState.Error(errorResponse.message)
                }
            }

            override fun onFailure(call: Call<ErrorResponse>, t: Throwable) {
               registerResult.value = ResultState.Error(t.message.toString())
            }
        })

        return registerResult
    }

    fun login(loginRequest: LoginRequest): LiveData<ResultState<LoginResult>> {
        loginResult.value = ResultState.Loading
        val client = apiService.login(loginRequest)

        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if(response.isSuccessful){
                    val result = response.body()?.loginResult!!
                    loginResult.value = ResultState.Success(result)
                }else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java)
                    loginResult.value = ResultState.Error(errorResponse.message)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                registerResult.value = ResultState.Error(t.message.toString())
            }
        })

        return loginResult
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