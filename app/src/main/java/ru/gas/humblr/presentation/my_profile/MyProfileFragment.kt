package ru.gas.humblr.presentation.my_profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.gas.humblr.R
import ru.gas.humblr.databinding.FragmentMyProfileBinding
import ru.gas.humblr.domain.model.LoadingState
import ru.gas.humblr.domain.model.NavArgs
import ru.gas.humblr.domain.utils.AppUtils
import ru.gas.humblr.presentation.main.AuthViewModel

@AndroidEntryPoint
class MyProfileFragment : Fragment(), AppUtils {

    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!
    val adapter = FriendsListAdapter { userName -> onUserNameClicked(userName) }
    private val viewModel: MyProfileViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logoutButton.setOnClickListener {
            val builder = AlertDialog.Builder(binding.logoutButton.context)
            builder.setMessage(getString(R.string.want_logout))
                .setPositiveButton(
                    R.string.yes
                ) { _, _ ->
                    findNavController().popBackStack()
                    authViewModel.logout()
                    val sharedPrefs = activity?.getPreferences(Context.MODE_PRIVATE)
                    sharedPrefs?.edit()?.clear()?.apply()
                }
                .setNegativeButton(
                    R.string.no
                ) { _, _ ->

                }
            builder.create().show()
        }

        binding.friendsRecycler.adapter = adapter
        binding.deleteSavedButton.setOnClickListener {
            viewModel.deleteSavedThings(
                {
                    Toast.makeText(context, getString(R.string.deleted), Toast.LENGTH_SHORT).show()
                },
                {
                    Toast.makeText(context, getString(R.string.error_try_later), Toast.LENGTH_SHORT)
                        .show()
                })
        }
        viewModel.loadCurrentUserInfo()
        viewModel.loadFriends {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewModel.friendsInfo.collectLatest {
                    adapter.setData(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.currentUserInfo.collect { user ->
                binding.userName.text = user?.name
                binding.karma.text =
                    String.format(resources.getString(R.string.karma), user?.karma.toString())
                binding.created.text = String.format(
                    resources.getString(R.string.since),
                    user?.created?.toDate()
                )

                user?.icon?.let {
                    Glide
                        .with(binding.userIcon.context)
                        .load(it)
                        .centerCrop()
                        .into(binding.userIcon)
                }
            }
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

    private fun onUserNameClicked(userName: String) {
        val bundle = Bundle()
        bundle.putString(NavArgs.USERNAME.key, userName)
        findNavController().navigate(
            R.id.action_my_profile_to_user_profile,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}