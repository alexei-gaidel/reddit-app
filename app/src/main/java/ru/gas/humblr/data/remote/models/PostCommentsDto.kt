package ru.gas.humblr.data.remote.models

import android.util.Log
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.gas.humblr.domain.model.CommentListItem
import ru.gas.humblr.domain.model.PostItem

@JsonClass(generateAdapter = true)
data class PostCommentsResponse(
    val postResponse: PostResponse, val commentsResponse: CommentsResponse
)

@JsonClass(generateAdapter = true)
data class PostResponse(
    val data: PostData?
) {
    fun toSavedPostList(): List<PostItem> {

        val posts = this.data?.children
        val postList = mutableListOf<PostItem>()
        posts?.onEach {
            postList.add(
                PostItem(
                    author = it.data.author,
                    title = it.data.title,
                    body = it.data.selftext,
                    image = it.data.preview?.images?.first()?.source?.url,
                    score = it.data.score,
                    created = it.data.created,
                    numComments = it.data.num_comments,
                    id = it.data.name,
                    voted = it.data.likes
                )
            )
        }
        return postList
    }

    fun toPostItem(): PostItem {

        val post = this.data?.children?.first()

        post.apply {
            return PostItem(
                author = this?.data?.author,
                title = this?.data?.title,
                body = this?.data?.selftext,
                image = this?.data?.preview?.images?.first()?.source?.url,
                score = this?.data?.score,
                created = this?.data?.created,
                numComments = this?.data?.num_comments,
                id = this?.data?.name,
                voted = this?.data?.likes

            )
        }
    }
}

@JsonClass(generateAdapter = true)
data class CommentsResponse(
    val data: PostData?
) {

    fun toCommentList(): List<CommentListItem> {

        val comments = this.data?.children
        val allComments = mutableListOf<Comment>()
        val temp = mutableListOf<Comment>()
        comments?.let { comments ->
            temp.addAll(comments)
        }
        temp.forEach {
            allComments.addAll(getReplies(it))
        }

        return allComments.map {
            CommentListItem(
                author = it.data.author,
                body = it.data.body,
                score = it.data.score,
                created = it.data.created,
                name = it.data.name
            )
        }
    }

    private fun getReplies(comment: Comment): List<Comment> {
        val allComments = mutableListOf<Comment>()
        allComments.add(comment)
        comment.data.replies?.data?.children?.let { replies ->
            if (replies.isNotEmpty()) {
                replies.forEach { comment ->
                    allComments.addAll(getReplies(comment))
                }
            }
        }
        return allComments
    }
}


@JsonClass(generateAdapter = true)
data class PostCommentsDto(
    val data: PostData?
)

@JsonClass(generateAdapter = true)
data class PostData(
    val children: List<Comment> = listOf(), val after: String?
)

@JsonClass(generateAdapter = true)
data class Comment(
    val data: CommentData,

    )

@JsonClass(generateAdapter = true)
data class CommentData(
    val title: String? = null,
    val selftext: String? = null,
    @Json(name = "subreddit_name_prefixed") val subredditNamePrefixed: String?,
    val num_comments: Long?,
    val score: Long?,
    val likes: Boolean?,
    val author: String?,
    val name: String,
    @Json(name = "url") val postImg: String? = null,
    @Json(name = "created_utc") val created: Double?,
    val replies: Replies?,
    val body: String? = null,
    val preview: Preview?
)

@JsonClass(generateAdapter = true)
data class Replies(
    var data: PostData?

)
