package ru.gas.humblr.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.gas.humblr.domain.model.Subreddit

@JsonClass(generateAdapter = true)
data class SubredditDto(
    val kind: String,
    val data: SubredditData
) {
    fun toSubreddit(): Subreddit {
        return Subreddit(
            bannerImg = this.data.bannerImg,
            iconImg = this.data.iconImg,
            displayName = this.data.displayName,
            description = this.data.description,
            subscribers = this.data.subscribers,
            title = this.data.title,
            userIsSubscriber = this.data.userIsSubscriber,
            created = this.data.created
        )
    }
}


@JsonClass(generateAdapter = true)
data class SubredditData(
    @Json(name = "display_name")
    val displayName: String?,
    val title: String?,
    val subscribers: Long?,
    @Json(name = "created_utc")
    val created: Long?,
    @Json(name = "user_is_subscriber")
    val userIsSubscriber: Boolean,
    @Json(name = "banner_img")
    val bannerImg: String?,
    @Json(name = "icon_img")
    val iconImg: String?,
    @Json(name = "public_description")
    val description: String?
)

