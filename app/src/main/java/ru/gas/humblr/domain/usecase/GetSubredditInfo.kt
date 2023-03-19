package ru.gas.humblr.domain.usecase

import ru.gas.humblr.data.remote.RemoteRepository
import ru.gas.humblr.domain.model.Subreddit
import javax.inject.Inject

class GetSubredditInfo @Inject constructor(private val remoteRepository: RemoteRepository) {
    suspend operator fun invoke(subreddit: String): Subreddit {
        return remoteRepository.getSingleSubreddit(subreddit)
    }
}