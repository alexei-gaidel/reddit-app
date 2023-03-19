package ru.gas.humblr.domain.usecase

import ru.gas.humblr.data.remote.RemoteRepository
import ru.gas.humblr.domain.model.SubredditPostsItem
import javax.inject.Inject

class GetSavedThings @Inject constructor(private val remoteRepository: RemoteRepository) {
    suspend operator fun invoke(userName: String): List<String> {
        return remoteRepository.getSavedThings(userName)
    }
}