package ru.gas.humblr.domain.model

sealed class LoadingState<T> {
    class Loading<T> : LoadingState<T>()
    class Success<T> : LoadingState<T>()
    class Error<T> : LoadingState<T>()
}
