package ru.gas.humblr.presentation.subreddit_post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.gas.humblr.domain.model.CommentListItem
import ru.gas.humblr.domain.model.Direction
import ru.gas.humblr.domain.model.LoadingState
import ru.gas.humblr.domain.model.PostItem
import ru.gas.humblr.domain.usecase.GetPost
import ru.gas.humblr.domain.usecase.GetPostComments
import ru.gas.humblr.domain.usecase.GetVoted
import ru.gas.humblr.domain.usecase.SaveThing
import javax.inject.Inject

@HiltViewModel
class SubredditPostViewModel @Inject constructor(
    private val getPostComments: GetPostComments,
    private val getPost: GetPost,
    private val getVoted: GetVoted,
    private val saveThing: SaveThing,
) : ViewModel() {

    private val _loadingState = MutableStateFlow<LoadingState<Any?>>(LoadingState.Loading())
    val loadingState = _loadingState.asStateFlow()
    private val _comments = MutableStateFlow<List<CommentListItem>>(emptyList())
    val comments = _comments.asStateFlow()
    private val _post = MutableStateFlow<PostItem?>(null)
    val post = _post.asStateFlow()

    fun saveSinglePost(id: String?, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            kotlin.runCatching { saveThing(id) }.onSuccess {
                onSuccess()
            }.onFailure {
                onFailure()
                println(it.stackTrace)
                println(it.cause)
                println(it.localizedMessage)
            }
        }
    }

    fun saveSingleComment(id: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            kotlin.runCatching { saveThing(id) }.onSuccess {
                onSuccess()
            }.onFailure {
                onFailure()
                println(it.stackTrace)
                println(it.cause)
                println(it.localizedMessage)
            }
        }
    }

    fun castVote(id: String, direction: String, onSuccess: () -> Unit) {
        val dir = when (direction) {
            Direction.UP.dir -> 1
            Direction.DOWN.dir -> -1
            else -> 0
        }
        viewModelScope.launch {
            kotlin.runCatching { getVoted(id, dir) }.onSuccess {
                onSuccess()
            }.onFailure {
                println(it.stackTrace)
                println(it.cause)
                println(it.localizedMessage)
            }

        }
    }

    fun loadComments(post: String, onSuccess: (List<CommentListItem>) -> Unit) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading()
            kotlin.runCatching {
                getPostComments(post)
            }.onSuccess {
                val firstComments = it.take(2).orEmpty()
                onSuccess(firstComments)
                _loadingState.value = LoadingState.Success()
                _comments.value = it
            }.onFailure {
                _loadingState.value = LoadingState.Error()
            }
        }
    }

    fun loadPost(post: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                getPost(post)
            }.onSuccess {
                _post.value = it
            }.onFailure {
                _loadingState.value = LoadingState.Error()
            }
        }
    }
}