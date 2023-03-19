package ru.gas.humblr.domain.model

data class PostItem(
val author: String?,
val title: String?,
val body: String?,
val image: String?,
val score: Long?,
val created: Double?,
val numComments: Long?,
val voted: Boolean?,
val id: String?
)

