package com.enrico.story_app.data.remote.request

data class RegisterRequest (
    val name: String,
    val email: String,
    val password: String,
)