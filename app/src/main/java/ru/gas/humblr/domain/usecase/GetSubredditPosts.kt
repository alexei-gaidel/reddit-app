package ru.gas.humblr.domain.usecase

import ru.gas.humblr.data.remote.RemoteRepository
import ru.gas.humblr.domain.model.SubredditListItem
import ru.gas.humblr.domain.model.SubredditPostsItem
import javax.inject.Inject

class GetSubredditPosts @Inject constructor(private val remoteRepository: RemoteRepository) {
    suspend operator fun invoke(subreddit: String): List<SubredditPostsItem> {
        return remoteRepository.getSubredditPosts(subreddit)
    }
}