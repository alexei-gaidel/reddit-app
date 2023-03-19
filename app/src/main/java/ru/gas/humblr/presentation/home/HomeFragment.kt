package ru.gas.humblr.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.gas.humblr.R
import ru.gas.humblr.databinding.FragmentHomeBinding
import ru.gas.humblr.domain.model.NavArgs
import ru.gas.humblr.domain.model.SubredditListItem
import ru.gas.humblr.domain.model.TabState

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    private val viewModel: HomeViewModel by viewModels()
    val adapter = NewSubredditListAdapter({ subreddit -> onPostClick(subreddit) },
        { isSubscribed, subreddit -> onSubscribeClick(isSubscribed, subreddit) })
    private val pagingAdapter = NewSubredditListAdapter({ subreddit -> onPostClick(subreddit) },
        { isSubscribed, subreddit -> onSubscribeClick(isSubscribed, subreddit) })


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        navView?.isVisible = true

        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT

        binding.recycler.adapter = pagingAdapter
        if (binding.newPosts.isChecked) {
            loadNew()
        } else loadPopular()

        binding.selector.setOnCheckedStateChangeListener { chipGroup, _ ->
            if (chipGroup.checkedChipId == R.id.newPosts) {
                loadNew()
            }
            if (chipGroup.checkedChipId == R.id.popularIPosts) {
                loadPopular()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                pagingAdapter.loadStateFlow.collect {
                    binding.mainProgress.isVisible = it.source.refresh is LoadState.Loading
                    binding.recycler.isVisible = it.source.refresh is LoadState.NotLoading
                    binding.bottomProgress.isVisible = it.source.append is LoadState.Loading
                }
            }
        }
    }

    private fun onSubscribeClick(isSubscribed: Boolean, subreddit: SubredditListItem) {

        if (isSubscribed) {
            viewModel.getUserUnsubscribed(subreddit.id)

        } else {
            viewModel.getUserSubscribed(subreddit.id)
        }
    }

    private fun onPostClick(item: SubredditListItem) {
        val bundle = Bundle()
        bundle.putString(NavArgs.SUBREDDIT.key, item.id)
        findNavController().popBackStack(R.id.navigation_subreddit, true)
        findNavController().navigate(R.id.action_home_to_subreddit, bundle)
    }

    private fun loadNew() {
        binding.recycler.isVisible = false
        viewModel.loadPagedNewSubreddits().onEach { pagingData ->
            pagingAdapter.submitData(pagingData)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun loadPopular() {
        binding.recycler.isVisible = false
        viewModel.loadPagedPopularSubreddits().onEach { pagingData ->
            pagingAdapter.submitData(pagingData)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }


    override fun onResume() {
        super.onResume()
        TabState.selectedPosition?.let { binding.selector.check(it) }
    }

    override fun onStop() {
        super.onStop()
        TabState.selectedPosition = binding.selector.checkedChipId
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}