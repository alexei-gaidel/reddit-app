package ru.gas.humblr.presentation.onboarding

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.gas.humblr.R
import ru.gas.humblr.databinding.FragmentAuthScreenBinding
import ru.gas.humblr.domain.model.NavArgs
import ru.gas.humblr.presentation.main.AuthViewModel


@AndroidEntryPoint
class AuthScreen : Fragment() {

    private var _binding: FragmentAuthScreenBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAuthScreenBinding.inflate(inflater, container, false)
        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_viewpager_to_main)
            authViewModel.onBoardingState.value = true
            val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
            sharedPrefs.edit().putBoolean(NavArgs.ONBOARDED.key, true).apply()
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
//    companion object{
//        const val ONBOARDED = "onboarded"
//    }
}