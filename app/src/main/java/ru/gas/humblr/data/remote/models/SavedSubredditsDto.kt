package ru.gas.humblr.data.remote.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SavedSubredditsDto(
    val data: DataX
)

