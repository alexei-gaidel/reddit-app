package ru.gas.humblr.domain.model

data class User(
    val name: String?,
    val created: Double?,
    val isFriend: Boolean?,
    val icon: String?,
    val karma: Long?
)
