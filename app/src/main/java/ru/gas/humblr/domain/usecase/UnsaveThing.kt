package ru.gas.humblr.domain.usecase

import ru.gas.humblr.data.remote.RemoteRepository
import javax.inject.Inject

class UnsaveThing @Inject constructor(private val remoteRepository: RemoteRepository) {
    suspend operator fun invoke(comment: String?) {
        return remoteRepository.unsaveThing(comment)
    }
}