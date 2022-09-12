package com.enrico.story_app.presentation.ui.add

import androidx.lifecycle.ViewModel
import com.enrico.story_app.data.remote.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel (private val storyRepository: StoryRepository): ViewModel() {
    fun uploadStory(token: String, file: MultipartBody.Part, description: RequestBody)  = storyRepository.uploadStory(token, file, description)
}