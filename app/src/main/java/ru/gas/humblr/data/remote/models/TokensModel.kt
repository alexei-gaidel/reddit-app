package ru.gas.humblr.data.remote.models

data class TokensModel(
    val accessToken: String,
    val refreshToken: String,
    val idToken: String
)
