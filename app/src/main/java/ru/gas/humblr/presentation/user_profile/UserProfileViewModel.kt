package ru.gas.humblr.presentation.user_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.gas.humblr.data.remote.RemoteRepository
import ru.gas.humblr.domain.model.CommentListItem
import ru.gas.humblr.domain.model.LoadingState
import ru.gas.humblr.domain.model.User
import ru.gas.humblr.domain.usecase.AddToFriends
import ru.gas.humblr.domain.usecase.GetUserInfo
import ru.gas.humblr.domain.usecase.RemoveFromFriends
import ru.gas.humblr.domain.usecase.SaveThing
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val getUserInfo: GetUserInfo,
    private val addToFriends: AddToFriends,
    private val removeFromFriends: RemoveFromFriends,
    private val repository: RemoteRepository,
    private val saveThing: SaveThing
) : ViewModel() {

    private val _loadingState = MutableStateFlow<LoadingState<Any?>>(LoadingState.Loading())
    val loadingState = _loadingState.asStateFlow()
    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo = _userInfo.asStateFlow()

    fun saveSingleComment(id: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            kotlin.runCatching { saveThing(id) }.onSuccess {
                onSuccess()
            }.onFailure {
                onFailure()
            }
        }
    }

    fun loadPagedComments(userName: String): Flow<PagingData<CommentListItem>> {
        val userCommentsListDataSource = UserCommentsListDataSource(repository, userName)
        return Pager(config = PagingConfig(pageSize = UserCommentsListDataSource.PAGE_SIZE),
            pagingSourceFactory = { userCommentsListDataSource }).flow
    }


    fun loadUserInfo(userName: String) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading()
            kotlin.runCatching {
                getUserInfo(userName)
            }.onSuccess {
                _loadingState.value = LoadingState.Success()
                _userInfo.value = it
            }.onFailure {
                _loadingState.value = LoadingState.Error()
            }
        }
    }


    fun makeFriends(userName: String?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            kotlin.runCatching {
                addToFriends(userName)
            }.onSuccess {
                    onSuccess()
                }.onFailure {
                    if (_userInfo.value?.isFriend == true) {
                        onSuccess()
                    }
                }
        }
    }

    fun unfriend(userName: String?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            kotlin.runCatching {
                removeFromFriends(userName)
                if (_userInfo.value?.isFriend == false) {
                    onSuccess()
                }
            }.onSuccess {
                    onSuccess()
                }.onFailure {
                    if (_userInfo.value?.isFriend == false) {
                        onSuccess()
                    }
                }
        }
    }
}