package ru.gas.humblr.data.remote

import com.google.gson.GsonBuilder
import ru.gas.humblr.data.remote.models.PostCommentsResponse
import ru.gas.humblr.data.remote.models.SubredditListItemDto
import ru.gas.humblr.data.remote.models.UserCommentsDto
import ru.gas.humblr.data.remote.utils.PostCommentsResponseDeserializer
import ru.gas.humblr.domain.model.*
import javax.inject.Inject


class RemoteRepository @Inject constructor(private val redditApi: RedditApi) {

    private suspend fun getFormattedPostComments(post: String): PostCommentsResponse {
        val rawResponse = redditApi.getRawPostComments(post)
        val formattedResponse = rawResponse.replace("\"replies\": \"\"", "\"replies\": null")
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(
            PostCommentsResponse::class.java,
            PostCommentsResponseDeserializer()
        )
        val gson = gsonBuilder.create()

        return gson.fromJson(formattedResponse, PostCommentsResponse::class.java)
    }

    suspend fun getUserComments(userName: String?, page: String)
            : UserCommentsDto {
        return redditApi.getUserComments(userName, page, COMMENTS_LIMIT)
    }

    suspend fun getFriends(): List<Friend> {
        return redditApi.getFriends().toFriendsList()
    }

    suspend fun getCurrentUser(): User {
        return redditApi.getCurrentUser().toUser()
    }

    suspend fun makeFriends(userName: String?) {
        val requestBody = "{\"name\": \"$userName\"}"
        redditApi.makeFriends(userName, requestBody)
    }

    suspend fun unfriend(userName: String?) {
        redditApi.unfriend(userName)
    }

    suspend fun getUserInfo(userName: String): User {
        return redditApi.getUser(userName).toUser()
    }

    suspend fun getPost(post: String): PostItem {
        return getFormattedPostComments(post).postResponse.toPostItem()
    }

    suspend fun getVoted(id: String, dir: Int) {
        return redditApi.vote(id, dir)
    }

    suspend fun getPostComments(post: String): List<CommentListItem> {
        return getFormattedPostComments(post).commentsResponse.toCommentList()
    }

    suspend fun saveThing(id: String?) {
        return redditApi.save(id)
    }

    suspend fun unsaveThing(id: String?) {
        return redditApi.unsave(id)
    }

    suspend fun getSubredditPosts(subreddit: String): List<SubredditPostsItem> {
        return redditApi.getSubredditPosts(subreddit).toSubredditPostsList()
    }

    suspend fun getSavedThings(userName: String): List<String> {
        return redditApi.getAllSavedThings(userName).toThingsList()
    }

    suspend fun getSavedPosts(userName: String?): List<PostItem> {
        return redditApi.getSavedPosts(userName).toSavedPostList()
    }

    suspend fun getSavedComments(userName: String?): List<CommentListItem> {
        return redditApi.getSavedComments(userName).toSavedCommentList()
    }

    suspend fun getSingleSubreddit(id: String): Subreddit {
        return redditApi.getSubreddit(id).toSubreddit()
    }

    suspend fun getSubscription(subreddit: String?): Boolean {
        return redditApi.getSubreddit(subreddit).data.userIsSubscriber
    }

    suspend fun getSubscribed(subreddit: String?) {
        return redditApi.subscribe(subreddit)
    }

    suspend fun getUnsubscribed(subreddit: String?) {
        return redditApi.unsubscribe(subreddit)
    }

    suspend fun getNewSubreddits(page: String): SubredditListItemDto {
        return redditApi.getNewSubreddits(PAGES_LIMIT, page)
    }

    suspend fun getPopularSubreddits(page: String): SubredditListItemDto {
        return redditApi.getPopularSubreddits(PAGES_LIMIT, page)
    }

    companion object {
        private const val PAGES_LIMIT = 12
        private const val COMMENTS_LIMIT = 50
    }
}