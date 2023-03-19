package ru.gas.humblr.domain.usecase

import ru.gas.humblr.data.remote.RemoteRepository
import ru.gas.humblr.domain.model.User
import javax.inject.Inject

class GetCurrentUserInfo @Inject constructor(private val remoteRepository: RemoteRepository) {
    suspend operator fun invoke(): User {
        return remoteRepository.getCurrentUser()
    }
}
