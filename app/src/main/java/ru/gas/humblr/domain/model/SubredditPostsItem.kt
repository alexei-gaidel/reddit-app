package ru.gas.humblr.domain.model

data class SubredditPostsItem(
    val author: String?,
    val title: String?,
    val img: String?,
    val id: String?,
    var subscribed: Boolean?,
    val postId: String?
)
