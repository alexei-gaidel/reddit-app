package ru.gas.humblr.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import ru.gas.humblr.R
import ru.gas.humblr.databinding.FragmentSecondScreenBinding

class SecondScreen : Fragment() {
    private var _binding: FragmentSecondScreenBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondScreenBinding.inflate(inflater, container, false)
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        binding.skipButton.setOnClickListener {
            viewPager?.currentItem = 2
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }
}