package ru.gas.humblr.presentation.favorite

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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.gas.humblr.R
import ru.gas.humblr.databinding.FragmentFavoriteBinding
import ru.gas.humblr.domain.model.*
import ru.gas.humblr.presentation.subreddit_post_all_comments.CommentsListAdapter

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteViewModel by viewModels()
    private val postsAdapter = SavedPostsListAdapter { userName -> onPostClick(userName) }
    private val commentsAdapter =
        CommentsListAdapter({ userName -> navigateToUserProfile(userName) }, { saveComment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (binding.chipPosts.isChecked) {
            loadPosts()
        } else loadComments()

        binding.postsCommentsSelector.setOnCheckedStateChangeListener { chipGroup, _ ->
            if (chipGroup.checkedChipId == R.id.chipPosts) {
                loadPosts()
            }
            if (chipGroup.checkedChipId == R.id.chipComments) {
                loadComments()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadingState.collect { state ->
                when (state) {

                    is LoadingState.Loading -> {
                        binding.progress.isVisible = true
                    }
                    is LoadingState.Success -> {
                        binding.progress.isVisible = false
                        binding.postListRecycler.isVisible = true
                    }
                    is LoadingState.Error -> {
                        binding.progress.isVisible = false

                        Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show()

                    }
                }

            }
        }

    }

    private fun onPostClick(item: String) {
        val bundle = Bundle()
        bundle.putString(POST_ID, item)
        findNavController().navigate(
            R.id.action_favorite_to_subreddit_post, bundle
        )

    }

    private fun saveComment() {
        Toast.makeText(context, getString(R.string.comment_already_saved), Toast.LENGTH_SHORT)
            .show()


    }

    private fun navigateToUserProfile(userName: String?) {
        val bundle = Bundle()
        bundle.putString(USERNAME, userName)
        findNavController().navigate(
            R.id.action_favorite_to_user_profile, bundle
        )
    }

    private fun loadPosts() {
        viewModel.loadSavedPosts()
        binding.postListRecycler.adapter = postsAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.savedPosts.collectLatest {
                postsAdapter.setData(it)
            }
        }
    }

    private fun loadComments() {
        viewModel.loadSavedComments()
        binding.postListRecycler.adapter = commentsAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.savedComments.collectLatest {
                commentsAdapter.setData(it)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        ChipState.selectedPosition?.let { binding.postsCommentsSelector.check(it) }
    }

    override fun onStop() {
        super.onStop()
        ChipState.selectedPosition = binding.postsCommentsSelector.checkedChipId
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val USERNAME = "userName"
        const val POST_ID = "postId"
    }
}