package ru.gas.humblr.domain.usecase

import android.util.Log
import ru.gas.humblr.data.remote.RemoteRepository
import javax.inject.Inject

class GetUnsubscribed @Inject constructor(private val remoteRepository: RemoteRepository) {
    suspend operator fun invoke(id: String?) {
        remoteRepository.getUnsubscribed(id)
    }
}