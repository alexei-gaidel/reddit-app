package ru.gas.humblr.domain.usecase

import ru.gas.humblr.data.remote.RemoteRepository
import ru.gas.humblr.domain.model.Friend
import javax.inject.Inject

class GetFriends @Inject constructor(private val remoteRepository: RemoteRepository) {
    suspend operator fun invoke(): List<Friend> {
        return remoteRepository.getFriends()
    }
}