package ru.gas.humblr.presentation.subreddit_post

import android.graphics.Paint
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
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import ru.gas.humblr.R
import ru.gas.humblr.databinding.FragmentSubredditPostBinding
import ru.gas.humblr.domain.model.LoadingState
import ru.gas.humblr.domain.model.NavArgs
import ru.gas.humblr.domain.utils.AppUtils
import ru.gas.humblr.presentation.subreddit_post_all_comments.CommentsListAdapter

@AndroidEntryPoint
class SubredditPostFragment : Fragment(), AppUtils {

    private var _binding: FragmentSubredditPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SubredditPostViewModel by viewModels()
    private val adapter = CommentsListAdapter({ userName -> navigateToUserProfile(userName) },
        { comment -> saveComment(comment) })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSubredditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val postId = arguments?.getString(NavArgs.POST.key)
        postId?.let {
            viewModel.loadPost(it)

            binding.commentsRecycler.adapter = adapter
            viewModel.loadComments(it) { firstComments ->
                adapter.setData(firstComments)
            }

        }


        binding.myToolbar.setNavigationOnClickListener {
            findNavController().popBackStack(
                R.id.navigation_subreddit_post, inclusive = true
            )
        }


        viewLifecycleOwner.lifecycleScope.launchWhenCreated {

            viewModel.loadingState.collect { state ->
                when (state) {

                    is LoadingState.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    is LoadingState.Success -> {

                        binding.mainView.isVisible = true
                        binding.progressBar.isVisible = false

                    }
                    is LoadingState.Error -> {
                        binding.progressBar.isVisible = false
                        Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.post.collect { post ->
                post?.numComments?.let {
                    if (it > FIRST_COMMENTS_COUNT) binding.showAllButtonLayout.isVisible = true
                }
                val votingState = MutableStateFlow(post?.voted)
                when (votingState.value) {
                    true -> {
                        binding.upArrow.setImageResource(R.drawable.arrow_up_checked)
                    }
                    false -> {
                        binding.downArrow.setImageResource(R.drawable.arrow_down_checked)
                    }
                    else -> {}
                }
                binding.saveText.setOnClickListener {
                    viewModel.saveSinglePost(post?.id, {
                        Toast.makeText(context, R.string.post_saved, Toast.LENGTH_SHORT).show()
                    }, {
                        Toast.makeText(context, R.string.saving_failed, Toast.LENGTH_SHORT).show()
                    })
                }


                binding.showAllButton.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString(NavArgs.POST.key, postId)
                    findNavController().navigate(
                        R.id.action_subreddit_post_to_post_all_comments, bundle
                    )
                }
                binding.postTitle.text = post?.title
                binding.author.paintFlags = binding.author.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                binding.author.text = post?.author
                binding.author.setOnClickListener {
                    navigateToUserProfile(post?.author)
                }
                binding.score.text = post?.score.toString()
                post?.body?.let {
                    binding.fullPost.isVisible = true
                    binding.fullPost.text = it
                }
                post?.image?.let {
                    binding.titleImg.isVisible = true
                    Glide.with(binding.titleImg.context).load(it).centerCrop()
                        .into(binding.titleImg)
                }
                var score = post?.score ?: 0

                binding.upArrow.setOnClickListener {
                    post?.id?.let {
                        when (votingState.value) {
                            true -> {
                                score -= INC_IF_UNVOTED
                                viewModel.castVote(it, UNVOTE) {
                                    votingState.value = null
                                    binding.score.text = score.toString()
                                    binding.upArrow.setImageResource(R.drawable.arrow_up)
                                }
                            }

                            false -> {
                                viewModel.castVote(it, UP) {
                                    votingState.value = true
                                    score += INC_IF_VOTED
                                    binding.score.text = score.toString()
                                    binding.upArrow.setImageResource(R.drawable.arrow_up_checked)
                                    binding.downArrow.setImageResource(R.drawable.arrow_down)

                                }
                            }
                            null -> {
                                viewModel.castVote(it, UP) {
                                    votingState.value = true
                                    score += INC_IF_UNVOTED
                                    binding.score.text = score.toString()
                                    binding.upArrow.setImageResource(R.drawable.arrow_up_checked)
                                }
                            }
                        }
                    }
                }

                binding.downArrow.setOnClickListener {

                    post?.id?.let {
                        when (votingState.value) {
                            false -> {
                                viewModel.castVote(it, UNVOTE) {
                                    votingState.value = null
                                    score += INC_IF_UNVOTED
                                    binding.score.text = score.toString()
                                    binding.downArrow.setImageResource(R.drawable.arrow_down)

                                }
                            }

                            true -> {
                                viewModel.castVote(it, DOWN) {
                                    votingState.value = false
                                    score -= INC_IF_VOTED
                                    binding.score.text = score.toString()
                                    binding.upArrow.setImageResource(R.drawable.arrow_up)
                                    binding.downArrow.setImageResource(R.drawable.arrow_down_checked)

                                }
                            }
                            null -> {
                                viewModel.castVote(it, DOWN) {
                                    votingState.value = false
                                    score -= INC_IF_UNVOTED
                                    binding.score.text = score.toString()
                                    binding.downArrow.setImageResource(R.drawable.arrow_down_checked)
                                }
                            }
                        }
                    }
                }


                val comments = getFormattedNumber(post?.numComments ?: 0)
                binding.commentsCount.text = String.format(
                    resources.getString(R.string.comments), comments
                )

                post?.created?.let { dateLong ->
                    binding.created.text = String.format(
                        resources.getString(
                            R.string.created, dateLong.toDate()
                        )
                    )
                }
            }
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

    private fun navigateToUserProfile(userName: String?) {
        val bundle = Bundle()
        bundle.putString(NavArgs.USERNAME.key, userName)
        findNavController().navigate(
            R.id.action_subreddit_post_to_user_profile, bundle
        )
    }

    companion object {
        const val DOWN = "down"
        const val UP = "up"
        const val UNVOTE = "unvote"

        //If comments > 2 "Show All" button
        const val FIRST_COMMENTS_COUNT = 2

        // Score increment if user didn't voted
        const val INC_IF_UNVOTED = 1

        // Score increment if user already voted in opposite direction (up to down, down to up)
        const val INC_IF_VOTED = 2
    }
}