package ru.gas.humblr.domain.usecase

import ru.gas.humblr.data.remote.RemoteRepository
import javax.inject.Inject

class SaveThing @Inject constructor(private val remoteRepository: RemoteRepository) {
    suspend operator fun invoke(post: String?) {
        return remoteRepository.saveThing(post)
    }
}

