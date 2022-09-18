package com.enrico.story_app.data

import com.enrico.DataDummy
import com.enrico.story_app.data.remote.request.LoginRequest
import com.enrico.story_app.data.remote.request.RegisterRequest
import com.enrico.story_app.data.remote.response.ErrorResponse
import com.enrico.story_app.data.remote.response.LoginResponse
import com.enrico.story_app.data.remote.response.StoriesResponse
import com.enrico.story_app.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiService: ApiService {
    private val dummyErrorResponse = DataDummy.generateDummyErrorResponse()
    private val dummyLoginResult = DataDummy.generateDummyLoginResponse()
    private val dummyStoriesResponse = DataDummy.generateDummyStoryResponse()


    override suspend fun register(registerRequest: RegisterRequest): ErrorResponse {
        return dummyErrorResponse
    }

    override suspend fun login(loginRequest: LoginRequest): LoginResponse {
        return dummyLoginResult
    }

    override suspend fun getStories(token: String, page: Int, location: Int): StoriesResponse {
       return dummyStoriesResponse
    }

    override suspend fun getStoriesForMaps(token: String, location: String): StoriesResponse {
        return dummyStoriesResponse
    }

    override suspend fun uploadImage(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): ErrorResponse {
       return dummyErrorResponse
    }

    override suspend fun uploadImageWithLocation(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody,
        lon: RequestBody
    ): ErrorResponse {
        return dummyErrorResponse
    }
}