package ru.gas.humblr.data.remote

import retrofit2.http.*
import ru.gas.humblr.data.remote.models.*

interface RedditApi {
    @GET("api/v1/me")
    suspend fun getCurrentUser(
    ) : CurrentUserDto

    @GET("/api/v1/me/friends")
    suspend fun getFriends() : FriendsListDto

    @GET("/user/{userName}/saved")
    suspend fun getAllSavedThings(@Path("userName") userName: String): SubredditPostsDto

    @GET("/user/{userName}/saved?type=links&&raw_json=1")
    suspend fun getSavedPosts(@Path("userName") userName: String?): PostResponse

    @GET("/user/{userName}/saved?type=comments&&raw_json=1")
    suspend fun getSavedComments(@Path("userName") userName: String?): SavedCommentsDto


//    @GET("r/all/new?raw_json=1")
    @GET("new?raw_json=1")
    suspend fun getNewSubreddits(@Query("limit") limit: Int?, @Query("after") page: String?)
            : SubredditListItemDto

    @GET("r/all/top?raw_json=1")
    suspend fun getPopularSubreddits(@Query("limit") limit: Int?, @Query("after") page: String?)
            : SubredditListItemDto

    @GET("/r/{subreddit}/about")
    suspend fun getSubreddit(@Path("subreddit") subreddit: String?): SubredditDto

    @POST("/api/subscribe?action=sub")
    suspend fun subscribe(@Query("sr_name") id: String?)

    @POST("/api/subscribe?action=unsub")
    suspend fun unsubscribe(@Query("sr_name") id: String?)

    @GET("/r/{subreddit}?raw_json=1")
    suspend fun getSubredditPosts(@Path("subreddit") subreddit: String): SubredditPostsDto

    @GET("/comments/{post}?raw_json=1")
    suspend fun getRawPostComments(@Path("post") post: String): String

    @POST("/api/vote")
    suspend fun vote(@Query("id") id: String, @Query("dir") dir: Int)

    @POST("/api/save")
    suspend fun save(@Query("id") id: String?)

    @POST("/api/unsave")
    suspend fun unsave(@Query("id") id: String?)

    @GET("/user/{userName}/about?raw_json=1")
    suspend fun getUser(@Path("userName") userName: String): UserDto

    @PUT("/api/v1/me/friends/{userName}")
    suspend fun makeFriends (@Path("userName") userName:String?, @Body requestBody: String)

    @DELETE("/api/v1/me/friends/{userName}")
    suspend fun unfriend (@Path("userName") userName:String?)

    @GET("/user/{userName}/comments?raw_json=1")
    suspend fun getUserComments (@Path("userName") userName:String?, @Query("after")page:String) : UserCommentsDto

    companion object {
        val BASE_URL = "https://oauth.reddit.com"
    }
}
