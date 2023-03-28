package ru.gas.humblr.presentation.onboarding

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.gas.humblr.R
import ru.gas.humblr.domain.model.NavArgs

class SplashFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPrefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
            val onBoarded = sharedPrefs.getBoolean(NavArgs.ONBOARDED.key, false)
            if (onBoarded) {
                findNavController().navigate(R.id.navigation_main)

            } else {
                findNavController().navigate(R.id.action_splash_to_viewpager)
            }
        }, 1000)

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
}