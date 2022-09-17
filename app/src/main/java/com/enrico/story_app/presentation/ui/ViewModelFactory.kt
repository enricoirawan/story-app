package com.enrico.story_app.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.enrico.story_app.data.remote.repository.AuthRepository
import com.enrico.story_app.data.remote.repository.StoryRepository
import com.enrico.story_app.di.Injection
import com.enrico.story_app.presentation.ui.add.AddViewModel
import com.enrico.story_app.presentation.ui.home.HomeViewModel
import com.enrico.story_app.presentation.ui.login.LoginViewModel
import com.enrico.story_app.presentation.ui.maps.MapsViewModel
import com.enrico.story_app.presentation.ui.register.RegisterViewModel

class ViewModelFactory private constructor(private val authRepository: AuthRepository, private val storyRepository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authRepository) as T
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(authRepository) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
            return AddViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideAuthRepository(), Injection.provideStoryRepository())
            }.also { instance = it }
    }
}