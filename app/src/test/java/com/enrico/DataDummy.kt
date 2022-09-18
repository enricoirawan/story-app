package com.enrico

import com.enrico.story_app.data.database.StoryEntity
import com.enrico.story_app.data.remote.request.LoginRequest
import com.enrico.story_app.data.remote.request.RegisterRequest
import com.enrico.story_app.data.remote.response.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object DataDummy {
    fun generateDummyToken() = "token"

    fun generateDummyStoryEntity(): List<StoryEntity> {
        val storyList = ArrayList<Story>()
        val storyEntityList = ArrayList<StoryEntity>()
        for (i in 0..10) {
            val story = Story(
                "1",
                "Testing Name",
                "Testing description",
                "Testing.jpg",
                "2022-01-01",
                -1.232,
                345.002
            );
            storyList.add(story)
        }

        storyList.forEach { story ->
            storyEntityList.add(
                StoryEntity(story.userId, story.name, story.description,
                    story.photoUrl, story.createdAt, story.lat, story.lon)
            )
        }

        return storyEntityList
    }

    fun generateDummyStoryResponse(): StoriesResponse {
        val storyList = ArrayList<Story>()
        for (i in 0..10) {
            val story = Story(
                "1",
                "Testing Name",
                "Testing description",
                "Testing.jpg",
                "2022-01-01",
                -1.232,
                345.002
            );
            storyList.add(story)
        }

        return StoriesResponse(false, "success", storyList)
    }

    fun generateDummyErrorResponse(): ErrorResponse {
        return ErrorResponse(false, "Register success")
    }

    fun generateDummyLoginResponseResult(): LoginResult {
        return LoginResult("1", "Testing name", "token")
    }

    fun generateDummyLoginResponse(): LoginResponse {
        return LoginResponse(false, "Success", generateDummyLoginResponseResult())
    }

    fun generateDummyRegisterRequest(): RegisterRequest {
        return RegisterRequest("testing name", "test@gmail.com", "123456")
    }

    fun generateDummyLoginRequest(): LoginRequest {
        return LoginRequest("test@gmail.com", "123456")
    }

    fun generateDummyMultipartFile(): MultipartBody.Part {
        val dummyText = "text"
        return MultipartBody.Part.create(dummyText.toRequestBody())
    }

    fun generateDummyRequestBody(): RequestBody {
        return "text".toRequestBody("text/plain".toMediaType())
    }
}