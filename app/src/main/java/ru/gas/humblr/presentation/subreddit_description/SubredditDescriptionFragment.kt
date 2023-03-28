package ru.gas.humblr.presentation.subreddit_description

import android.content.Intent
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
import ru.gas.humblr.R
import ru.gas.humblr.databinding.FragmentSubredditDescriptionBinding
import ru.gas.humblr.domain.model.LoadingState
import ru.gas.humblr.domain.model.NavArgs
import ru.gas.humblr.domain.utils.AppUtils


@AndroidEntryPoint
class SubredditDescriptionFragment : Fragment(), AppUtils {

    private var _binding: FragmentSubredditDescriptionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SubredditDescriptionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSubredditDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val subredditId = arguments?.getString(NavArgs.SUBREDDIT.key)
        binding.toolbarTitle.text =
            String.format(resources.getString(R.string.toolbar_info), subredditId)

        binding.myToolbar.setNavigationOnClickListener {
            findNavController().popBackStack(
                R.id.navigation_subreddit_description, inclusive = true
            )
        }

        binding.shareIcon.setOnClickListener {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_TEXT, REDDIT_SHARE_URI + subredditId)
            startActivity(Intent.createChooser(i, getString(R.string.share_url)))
        }

        if (subredditId != null) {
            viewModel.loadSubredditInfo(subredditId)
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewModel.subredditInfo.collect {
                    val subredditInfo = it

                    if (subredditInfo?.bannerImg != null) {
                        binding.bannerImage.isVisible = true
                        Glide.with(binding.bannerImage.context).load(subredditInfo.bannerImg)
                            .centerCrop().into(binding.bannerImage)
                    }

                    if (subredditInfo?.iconImg != null) {
                        binding.iconImage.isVisible = true
                        Glide.with(binding.iconImage.context).load(subredditInfo.iconImg)
                            .centerCrop().into(binding.iconImage)
                    }

                    var isSubscribedString =
                        if (subredditInfo?.userIsSubscriber == true) getString(R.string.yes) else getString(
                            R.string.no
                        )
                    var subscribers = "0"
                    subredditInfo?.subscribers?.let { count ->
                        subscribers = getFormattedNumber(count)
                    }
                    with(binding) {
                        subredditTitle.text = subredditInfo?.description
                        description.text = subredditInfo?.description
                        subredditTitle.text = subredditInfo?.title
                        subscribersCount.text = String.format(
                            resources.getString(R.string.subscribers), subscribers
                        )
                        subredditInfo?.created?.let { dateLong ->
                            created.text = String.format(
                                resources.getString(
                                    R.string.created, dateLong.toDate()

                                )
                            )
                        }

                        subscriberStatus.text = String.format(
                            resources.getString(R.string.you_are_subscribed), isSubscribedString
                        )

                        changeSubscriptionButton.setOnClickListener {
                            val subscribeState = viewModel.subscribeState.value
                            if (subscribeState) viewModel.getUserUnsubscribed(subredditId) else viewModel.getUserSubscribed(
                                subredditId
                            )
                            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                                viewModel.subscribeState.collect { subscribeState ->

                                    when (subscribeState) {
                                        true -> {
                                            isSubscribedString = resources.getString(R.string.yes)
                                            subscriberStatus.text = String.format(
                                                resources.getString(R.string.you_are_subscribed),
                                                isSubscribedString
                                            )
                                        }
                                        false -> {
                                            isSubscribedString = resources.getString(R.string.no)
                                            subscriberStatus.text = String.format(
                                                resources.getString(R.string.you_are_subscribed),
                                                isSubscribedString
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                        viewModel.loadingState.collect { state ->
                            when (state) {

                                is LoadingState.Loading -> {
                                    binding.infoProgress.isVisible = true
                                }
                                is LoadingState.Success -> {
                                    binding.infoProgress.isVisible = false
                                    binding.mainView.isVisible = true
                                }
                                is LoadingState.Error -> {
                                    binding.infoProgress.isVisible = false
                                    Toast.makeText(
                                        context, R.string.loading_failed, Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REDDIT_SHARE_URI = "https://www.reddit.com/r/"
    }

}


