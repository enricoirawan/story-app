package com.enrico.story_app.presentation.ui.home

import androidx.lifecycle.ViewModel
import com.enrico.story_app.data.remote.repository.StoryRepository

class HomeViewModel (private val storyRepository: StoryRepository): ViewModel() {
    fun getStories(token: String)  = storyRepository.getStories(token)
}