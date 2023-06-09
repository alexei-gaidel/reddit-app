package ru.gas.humblr.domain.usecase

import ru.gas.humblr.data.remote.RemoteRepository
import ru.gas.humblr.domain.model.PostItem
import ru.gas.humblr.domain.model.SubredditPostsItem
import javax.inject.Inject

class GetSavedPosts @Inject constructor(private val remoteRepository: RemoteRepository) {
        suspend operator fun invoke(userName: String?): List<PostItem> {
            return remoteRepository.getSavedPosts(userName)
        }
    }
