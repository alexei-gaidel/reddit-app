package ru.gas.humblr.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import ru.gas.humblr.domain.model.User

@JsonClass(generateAdapter = true)
data class CurrentUserDto(

    @Json(name = "icon_img")
    val iconImg: String,

    @Json(name = "total_karma")
    val totalKarma: Long,

    val name: String,
    val created: Double,
    ) {
    fun toUser(): User {
        return User(
            name = this.name,
            created = this.created,
            isFriend = null,
            icon = this.iconImg,
            karma = this.totalKarma
        )
    }
}
