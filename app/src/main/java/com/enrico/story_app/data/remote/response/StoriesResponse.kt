package com.enrico.story_app.data.remote.response

import com.google.gson.annotations.SerializedName

data class StoriesResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val listStory: List<Story>
)