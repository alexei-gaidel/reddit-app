package ru.gas.humblr.presentation.subreddit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.gas.humblr.domain.model.LoadingState
import ru.gas.humblr.domain.model.SubredditPostsItem
import ru.gas.humblr.domain.usecase.GetSubredditPosts
import javax.inject.Inject

@HiltViewModel
class SubredditViewModel @Inject constructor(private val getSubredditPosts: GetSubredditPosts) :
    ViewModel() {

    private val _loadingState = MutableStateFlow<LoadingState<Any?>>(LoadingState.Loading())
    val loadingState = _loadingState.asStateFlow()
    private val _subredditPosts = MutableStateFlow<List<SubredditPostsItem>>(emptyList())
    val subredditPosts = _subredditPosts.asStateFlow()

    fun loadSubredditPosts(subreddit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadingState.value = LoadingState.Loading()
            kotlin.runCatching {
                getSubredditPosts(subreddit)
            }.onSuccess {
                _loadingState.value = LoadingState.Success()
                _subredditPosts.value = it
            }.onFailure {
                println(it.localizedMessage)
                _loadingState.value = LoadingState.Error()
            }
        }

    }
}
