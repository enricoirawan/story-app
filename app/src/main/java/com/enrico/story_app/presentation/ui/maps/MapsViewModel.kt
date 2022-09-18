package com.enrico.story_app.presentation.ui.maps

import androidx.lifecycle.ViewModel
import com.enrico.story_app.data.repository.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStoriesForMaps(token: String)  = storyRepository.getStoriesForMaps(token)
}