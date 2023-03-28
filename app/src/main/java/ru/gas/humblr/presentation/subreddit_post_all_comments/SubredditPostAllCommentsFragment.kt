package ru.gas.humblr.presentation.subreddit_post_all_comments

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
import ru.gas.humblr.R
import ru.gas.humblr.databinding.FragmentSubredditPostAllCommentsBinding
import ru.gas.humblr.domain.model.LoadingState
import ru.gas.humblr.domain.model.NavArgs
import ru.gas.humblr.domain.utils.AppUtils


@AndroidEntryPoint
class SubredditPostAllCommentsFragment : Fragment(), AppUtils {
    private var _binding: FragmentSubredditPostAllCommentsBinding? = null
    private val binding get() = _binding!!

    private val adapter = CommentsListAdapter({ userName -> onUserNameClicked(userName) },
        { comment -> saveComment(comment) })
    private val viewModel: SubredditPostAllCommentsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubredditPostAllCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postId = arguments?.getString(NavArgs.POST.key)

        binding.commentsRecycler.adapter = adapter
        binding.myToolbar.setNavigationOnClickListener {
            findNavController().popBackStack(
                R.id.navigation_post_all_comments, inclusive = true
            )
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.loadingState.collect { state ->
                when (state) {
                    is LoadingState.Loading -> {
                        binding.progress.isVisible = true
                    }
                    is LoadingState.Success -> {
                        binding.progress.isVisible = false
                    }
                    is LoadingState.Error -> {
                        binding.progress.isVisible = false
                        Toast.makeText(context, R.string.loading_failed, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        if (postId != null) {
            viewModel.loadComments(postId)
            viewModel.comments.onEach {
                adapter.setData(it)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveComment(comment: String?) {
        if (comment != null) {
            viewModel.saveSingleComment(comment, {
                Toast.makeText(context, getString(R.string.comment_saved), Toast.LENGTH_SHORT)
                    .show()
            }, { Toast.makeText(context, R.string.saving_failed, Toast.LENGTH_SHORT).show() })
        }
    }

    private fun onUserNameClicked(userName: String) {
        val bundle = Bundle()
        bundle.putString(NavArgs.USERNAME.key, userName)
        findNavController().navigate(R.id.action_ost_all_comments_to_user_profile, bundle)
    }
}