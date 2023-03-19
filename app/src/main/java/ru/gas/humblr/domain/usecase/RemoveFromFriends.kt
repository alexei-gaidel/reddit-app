package ru.gas.humblr.domain.usecase

import ru.gas.humblr.data.remote.RemoteRepository
import javax.inject.Inject

class RemoveFromFriends @Inject constructor(private val remoteRepository: RemoteRepository) {
    suspend operator fun invoke(userName: String?) {
        return remoteRepository.unfriend(userName)
    }
}
