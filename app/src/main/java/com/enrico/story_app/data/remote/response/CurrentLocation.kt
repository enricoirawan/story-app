package com.enrico.story_app.data.remote.response

data class CurrentLocation(
    val countryName: String?,
    var admin: String?,
    var latitude: Double?,
    var longitude: Double?
)
