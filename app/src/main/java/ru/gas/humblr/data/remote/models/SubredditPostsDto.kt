package ru.gas.humblr.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.gas.humblr.domain.model.SubredditPostsItem


@JsonClass(generateAdapter = true)
data class SubredditPostsDto(
    val data: DataX
) {
    fun toSubredditPostsList(): List<SubredditPostsItem> {
        return this.data.children.map { item ->
            SubredditPostsItem(
                author = item.data?.subredditNamePrefixed,
                title = item.data?.title,
                img = item.data?.preview?.images?.first()?.source?.url,
                id = item.data?.subreddit,
                subscribed = false,
                postId = item.data?.postId
            )

        }

    }

    fun toThingsList(): List<String> {
        val thingsList = mutableListOf<String>()
        this.data.children.onEach {
            it.data?.name?.let { thingsList.add(it) }
        }
        return thingsList

    }
}

@JsonClass(generateAdapter = true)
data class DataX(
    val children: List<ChildX>,
)

@JsonClass(generateAdapter = true)
data class ChildX(
    val kind: String?,
    val data: ChildDataX?
)

@JsonClass(generateAdapter = true)
data class ChildDataX(
    val subreddit: String?,
    val title: String?,
    @Json(name = "subreddit_name_prefixed")
    val subredditNamePrefixed: String?,
    @Json(name = "id")
    val postId: String?,
    val name: String?,
    val preview: Preview?,
    @Json(name = "subreddit_id")
    val subredditId: String?
)


@JsonClass(generateAdapter = true)
data class PreviewX(
    val images: List<Image>,
)

@JsonClass(generateAdapter = true)
data class ImagXe(
    val source: ResizedIconX?,
    val resolutions: List<ResizedIconX>?,
)

@JsonClass(generateAdapter = true)
data class ResizedIconX(
    val url: String,
    val width: Long,
    val height: Long
)


