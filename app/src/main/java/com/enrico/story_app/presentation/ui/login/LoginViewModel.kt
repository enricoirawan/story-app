package com.enrico.story_app.presentation.ui.login

import androidx.lifecycle.ViewModel
import com.enrico.story_app.data.remote.request.LoginRequest
import com.enrico.story_app.data.repository.AuthRepository

class LoginViewModel (private val authRepository: AuthRepository) : ViewModel() {
    fun login(loginRequest: LoginRequest) = authRepository.login(loginRequest)
}