package com.enrico.story_app.data.remote.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.enrico.story_app.data.ResultState
import com.enrico.story_app.data.remote.paging.StoryPagingSource
import com.enrico.story_app.data.remote.response.ErrorResponse
import com.enrico.story_app.data.remote.response.Story
import com.enrico.story_app.data.remote.retrofit.ApiService
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.net.SocketTimeoutException

class StoryRepository private constructor(
    private val apiService: ApiService,
) {
    fun getStories(token: String): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, "Bearer $token")
            }
        ).liveData
    }

    fun getStoriesForMaps(token: String): LiveData<ResultState<List<Story>>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.getStoriesForMaps("Bearer $token", "1")
            val stories = response.listStory
            emit(ResultState.Success(stories))
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

    fun uploadStory(token: String, file: MultipartBody.Part, description: RequestBody):
            LiveData<ResultState<ErrorResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.uploadImage("Bearer $token", file, description)
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

    fun uploadStoryWithLocation(token: String, file: MultipartBody.Part,
                                description: RequestBody, lat: RequestBody, lon: RequestBody):
            LiveData<ResultState<ErrorResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val response = apiService.uploadImageWithLocation("Bearer $token", file, description, lat, lon)
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

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
    }
}