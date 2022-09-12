package com.enrico.story_app.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.enrico.story_app.data.ResultState
import com.enrico.story_app.data.remote.response.ErrorResponse
import com.enrico.story_app.data.remote.response.StoriesResponse
import com.enrico.story_app.data.remote.response.Story
import com.enrico.story_app.data.remote.retrofit.ApiService
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository private constructor(
    private val apiService: ApiService,
) {
    private val storyResults = MediatorLiveData<ResultState<List<Story>>>()
    private val uploadStoryResults = MediatorLiveData<ResultState<ErrorResponse>>()

    fun getStories(token: String): LiveData<ResultState<List<Story>>> {
        storyResults.value = ResultState.Loading
        val client = apiService.getStories("Bearer $token")

        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                if(response.isSuccessful){
                    val listStory = response.body()!!.listStory
                    storyResults.value = ResultState.Success(listStory)
                }else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java)
                    storyResults.value = ResultState.Error(errorResponse.message)
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                storyResults.value = ResultState.Error(t.message.toString())
            }

        })

        return storyResults
    }

    fun uploadStory(token: String, file: MultipartBody.Part, description: RequestBody): LiveData<ResultState<ErrorResponse>> {
        uploadStoryResults.value = ResultState.Loading

        val client = apiService.uploadImage("Bearer $token", file, description)
        client.enqueue(object : Callback<ErrorResponse>{
            override fun onResponse(call: Call<ErrorResponse>, response: Response<ErrorResponse>) {
                if(response.isSuccessful){
                    val response = response.body()!!
                    uploadStoryResults.value = ResultState.Success(response)
                }else {
                    val errorResponse = Gson().fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java)
                    uploadStoryResults.value = ResultState.Error(errorResponse.message)
                }
            }

            override fun onFailure(call: Call<ErrorResponse>, t: Throwable) {
                uploadStoryResults.value = ResultState.Error(t.message.toString())
            }

        })
        return uploadStoryResults
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