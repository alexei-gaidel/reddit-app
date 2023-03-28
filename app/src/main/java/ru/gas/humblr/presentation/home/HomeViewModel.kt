package ru.gas.humblr.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.gas.humblr.data.remote.RemoteRepository
import ru.gas.humblr.domain.model.SubredditListItem
import ru.gas.humblr.domain.usecase.GetSubscribed
import ru.gas.humblr.domain.usecase.GetUnsubscribed
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSubscribed: GetSubscribed,
    private val getUnsubscribed: GetUnsubscribed,
    private val repository: RemoteRepository
) : ViewModel() {

    fun loadPagedNewSubreddits(): Flow<PagingData<SubredditListItem>> {
        val newListDataSource = NewSubredditsDataSource(repository)
        return Pager(config = PagingConfig(pageSize = NewSubredditsDataSource.PAGE_SIZE),
            pagingSourceFactory = { newListDataSource }).flow
    }

    fun loadPagedPopularSubreddits(): Flow<PagingData<SubredditListItem>> {
        val popListDataSource = PopSubredditsDataSource(repository)
        return Pager(config = PagingConfig(pageSize = PopSubredditsDataSource.PAGE_SIZE),
            pagingSourceFactory = { popListDataSource }).flow
    }

    fun getUserSubscribed(subreddit: String?) {
        viewModelScope.launch() {
            getSubscribed(subreddit)
        }
    }

    fun getUserUnsubscribed(subreddit: String?) {
        viewModelScope.launch() {
            getUnsubscribed(subreddit)
        }
    }


}