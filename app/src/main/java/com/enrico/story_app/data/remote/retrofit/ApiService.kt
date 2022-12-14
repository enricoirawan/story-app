package com.enrico.story_app.data.remote.retrofit

import com.enrico.story_app.data.remote.request.LoginRequest
import com.enrico.story_app.data.remote.request.RegisterRequest
import com.enrico.story_app.data.remote.response.ErrorResponse
import com.enrico.story_app.data.remote.response.LoginResponse
import com.enrico.story_app.data.remote.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @POST("register")
    suspend fun register(@Body registerRequest: RegisterRequest): ErrorResponse

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @GET("stories")
    suspend fun getStories(@Header("Authorization") token: String, @Query("page") page: Int,  @Query("size") location: Int): StoriesResponse

    @GET("stories")
    suspend fun getStoriesForMaps(@Header("Authorization") token: String, @Query("location") location: String): StoriesResponse

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): ErrorResponse

    @Multipart
    @POST("stories")
    suspend fun uploadImageWithLocation(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
    ): ErrorResponse
}