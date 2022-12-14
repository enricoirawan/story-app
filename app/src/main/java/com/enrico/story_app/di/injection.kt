package com.enrico.story_app.di

import android.content.Context
import com.enrico.story_app.data.database.AppDatabase
import com.enrico.story_app.data.remote.retrofit.ApiConfig
import com.enrico.story_app.data.repository.AuthRepository
import com.enrico.story_app.data.repository.StoryRepository

object Injection {
    fun provideAuthRepository(): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val database = AppDatabase.getDatabase(context)
        return StoryRepository.getInstance(apiService, database)
    }
}