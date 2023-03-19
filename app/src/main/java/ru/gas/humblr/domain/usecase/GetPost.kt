package ru.gas.humblr.domain.usecase

import ru.gas.humblr.data.remote.RemoteRepository
import ru.gas.humblr.domain.model.CommentListItem
import ru.gas.humblr.domain.model.PostItem
import javax.inject.Inject

class GetPost @Inject constructor(private val remoteRepository: RemoteRepository) {
        suspend operator fun invoke(post: String)
                : PostItem {
            return remoteRepository.getPost(post)
        }
    }
