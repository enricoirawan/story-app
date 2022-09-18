package com.enrico.story_app.presentation.ui.register

import androidx.lifecycle.ViewModel
import com.enrico.story_app.data.repository.AuthRepository
import com.enrico.story_app.data.remote.request.RegisterRequest

class RegisterViewModel (private val authRepository: AuthRepository): ViewModel() {
    fun register(registerRequest: RegisterRequest) = authRepository.register(registerRequest)
}