package ru.gas.humblr.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.gas.humblr.domain.model.CommentListItem

@JsonClass(generateAdapter = true)
data class SavedCommentsDto(
    val data: SavedCommentsData?
) {
    fun toSavedCommentList(): List<CommentListItem> {
        val comments = this.data?.children
        val commentsList = mutableListOf<CommentListItem>()
        comments?.onEach {
            commentsList.add(
                CommentListItem(
                    author = it.data.author,
                    body = it.data.body,
                    score = it.data.score,
                    created = it.data.created,
                    name = it.data.name
                )
            )
        }
        return commentsList
    }
}

@JsonClass(generateAdapter = true)
data class SavedCommentsData(
    val children: List<SavedComment> = listOf(), val after: String?

)

@JsonClass(generateAdapter = true)
data class SavedComment(
    val data: SavedCommentData,
)

@JsonClass(generateAdapter = true)
data class SavedCommentData(
    val score: Long?,
    val author: String?,
    val name: String,
    @Json(name = "created_utc") val created: Double?,
    val body: String?
)


