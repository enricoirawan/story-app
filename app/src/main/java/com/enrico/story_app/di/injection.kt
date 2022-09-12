package com.enrico.story_app.di

import com.enrico.story_app.data.remote.repository.AuthRepository
import com.enrico.story_app.data.remote.repository.StoryRepository
import com.enrico.story_app.data.remote.retrofit.ApiConfig

object Injection {
    fun provideAuthRepository(): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService)
    }

    fun provideStoryRepository(): StoryRepository {
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService)
    }
}