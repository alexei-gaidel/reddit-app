package ru.gas.humblr.presentation.user_profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.gas.humblr.R
import ru.gas.humblr.databinding.FragmentUserProfileBinding
import ru.gas.humblr.domain.model.LoadingState
import ru.gas.humblr.domain.model.NavArgs
import ru.gas.humblr.domain.utils.AppUtils

@AndroidEntryPoint
class UserProfileFragment : Fragment(), AppUtils {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserProfileViewModel by viewModels()
    private val pagingAdapter = UserCommentsListAdapter { comment -> saveComment(comment) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.commentsRecycler.adapter = pagingAdapter
        val userName = arguments?.getString(NavArgs.USERNAME.key)
        userName?.let {
            viewModel.loadUserInfo(userName)

            viewModel.loadPagedComments(it).onEach { pagingData ->
                pagingAdapter.submitData(pagingData)
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        }


        var isFriend = viewModel.userInfo.value?.isFriend

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.userInfo.collect { user ->

                binding.userName.text = user?.name
                val created = user?.created?.toDate()
                binding.created.text = String.format(resources.getString(R.string.since, created))
                val karma = user?.karma.toString()
                binding.karma.text = String.format(
                    resources.getString(R.string.karma),
                    karma
                )

                user?.icon?.let {
                    Glide
                        .with(binding.userIcon.context)
                        .load(it)
                        .centerCrop()
                        .into(binding.userIcon)
                }
                if (user?.isFriend == true) {
                    binding.makeFriendsButton.setImageResource(R.drawable.friend_icon_white)
                    binding.isFriend.text = resources.getString(R.string.is_a_friend)
                    binding.friendsBar.setCardBackgroundColor(
                        ContextCompat.getColor(binding.friendsBar.context, R.color.friends_bar)
                    )
                } else {
                    binding.makeFriendsButton.setImageResource(
                        R.drawable.not_friend_icon_white
                    )
                    binding.isFriend.text = resources.getString(R.string.not_a_friend)
                    binding.friendsBar.setCardBackgroundColor(
                        ContextCompat.getColor(binding.friendsBar.context, R.color.secondary)
                    )
                }


                binding.friendsBar.setOnClickListener {
                    if (isFriend == true) {
                        viewModel.unfriend(user?.name) {
                            isFriend = false
                            binding.makeFriendsButton.setImageResource(R.drawable.not_friend_icon_white)
                            binding.isFriend.text = resources.getString(R.string.not_a_friend)
                            binding.friendsBar.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    binding.friendsBar.context,
                                    R.color.secondary
                                )
                            )
                        }
                    } else {
                        viewModel.makeFriends(user?.name) {
                            isFriend = true
                            binding.makeFriendsButton.setImageResource(R.drawable.friend_icon_white)
                            binding.isFriend.text = resources.getString(R.string.is_a_friend)
                            binding.friendsBar.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    binding.friendsBar.context,
                                    R.color.friends_bar
                                )
                            )
                        }
                    }

                }
            }
        }

        binding.myToolbar.setNavigationOnClickListener {
            findNavController().popBackStack(
                R.id.navigation_user_profile,
                inclusive = true
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


    }

    private fun saveComment(comment: String?) {
        if (comment != null) {
            viewModel.saveSingleComment(comment, {
                Toast.makeText(context, getString(R.string.comment_saved), Toast.LENGTH_SHORT)
                    .show()
            }, { Toast.makeText(context, R.string.saving_failed, Toast.LENGTH_SHORT).show() })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
//    companion object {
//        const val USERNAME = "userName"
//    }

}