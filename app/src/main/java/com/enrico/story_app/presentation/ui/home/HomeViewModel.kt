package com.enrico.story_app.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.enrico.story_app.data.repository.StoryRepository

class HomeViewModel (private val storyRepository: StoryRepository): ViewModel() {
    fun getStories(token: String) = storyRepository.getStories(token).cachedIn(viewModelScope)
}