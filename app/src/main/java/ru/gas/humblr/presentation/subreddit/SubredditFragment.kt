package ru.gas.humblr.presentation.subreddit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.gas.humblr.databinding.FragmentSubredditBinding
import ru.gas.humblr.domain.model.LoadingState
import ru.gas.humblr.domain.model.NavArgs
import ru.gas.humblr.domain.model.SubredditPostsItem

@AndroidEntryPoint
class SubredditFragment : Fragment() {
    private var _binding: FragmentSubredditBinding? = null
    private val binding get() = _binding!!


    private val viewModel: SubredditViewModel by viewModels()
    val adapter = SubredditPostsAdapter { subreddit -> onItemClick(subreddit) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSubredditBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val subredditId = arguments?.getString(NavArgs.SUBREDDIT.key)
        binding.toolbarTitle.text = String.format(resources.getString(ru.gas.humblr.R.string.toolbar_feed), subredditId)


        binding.myToolbar.setNavigationOnClickListener {
            findNavController().popBackStack(ru.gas.humblr.R.id.navigation_subreddit, inclusive = true)
        }

        binding.informationImage.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(NavArgs.SUBREDDIT.key, subredditId)
            findNavController().navigate(ru.gas.humblr.R.id.action_subreddit_to_subreddit_description, bundle)
        }


        if (subredditId != null) {
            viewModel.loadSubredditPosts(subredditId)
        }
        viewModel.subredditPosts.onEach {
            adapter.setData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.subredditRecycler.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.loadingState.collect { state ->
                when (state) {

                    is LoadingState.Loading -> {
                        binding.postsProgress.isVisible = true
                    }
                    is LoadingState.Success -> {
                        binding.postsProgress.isVisible = false
                    }
                    is LoadingState.Error -> {
                        binding.postsProgress.isVisible = false

                        Toast.makeText(context, ru.gas.humblr.R.string.failed, Toast.LENGTH_SHORT).show()

                    }
                }

            }
        }


    }

        private fun onItemClick(item: SubredditPostsItem) {
            val bundle = Bundle()
            bundle.putString(NavArgs.POST.key, item.postId)
            findNavController().navigate(ru.gas.humblr.R.id.action_subreddit_to_subreddit_post, bundle)

        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object{
//        const val POST_ID = "postId"
//        const val SUBREDDIT_ID = "subredditId"
    }

}







