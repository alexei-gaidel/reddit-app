package ru.gas.humblr.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.gas.humblr.domain.model.CommentListItem

@JsonClass(generateAdapter = true)
data class UserCommentsDto(
    val data: UserCommentsData?
) {
    fun toUserComments(): List<CommentListItem> {
        val comments = mutableListOf<CommentListItem>()

        this.data?.children?.map {
            comments.add(
                CommentListItem(
                    author = it.data.author,
                    body = it.data.body,
                    score = it.data.score,
                    created = it.data.created,
                    name = it.data.name
                )
            )
        }
        return comments
    }
}


@JsonClass(generateAdapter = true)
data class UserCommentsData(
    val children: List<Comments> = listOf(), val after: String?
)

@JsonClass(generateAdapter = true)
data class Comments(
    val data: CommentsData,
)

@JsonClass(generateAdapter = true)
data class CommentsData(
    val title: String?,
    val selftext: String?,
    @Json(name = "subreddit_name_prefixed") val subredditNamePrefixed: String?,
    val num_comments: Long?,
    val score: Long?,
    val likes: Boolean?,
    val author: String?,
    val name: String,
    @Json(name = "url") val postImg: String?,
    @Json(name = "created_utc") val created: Double?,
    val body: String?,
    val preview: Preview?
)



