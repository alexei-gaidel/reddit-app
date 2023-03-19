package ru.gas.humblr.domain.usecase

import ru.gas.humblr.data.remote.RemoteRepository
import ru.gas.humblr.domain.model.CommentListItem
import ru.gas.humblr.domain.model.SubredditPostsItem
import javax.inject.Inject

class GetSavedComments @Inject constructor(private val remoteRepository: RemoteRepository) {
    suspend operator fun invoke(userName: String?): List<CommentListItem> {
        return remoteRepository.getSavedComments(userName)
    }
}