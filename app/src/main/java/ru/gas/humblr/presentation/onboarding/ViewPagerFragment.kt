package ru.gas.humblr.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.gas.humblr.databinding.FragmentViewpagerBinding

class ViewPagerFragment : Fragment() {
    private var _binding: FragmentViewpagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewpagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentList = arrayListOf(
            FirstScreen(), SecondScreen(), ThirdScreen(), AuthScreen()
        )

        val adapter =
            ViewPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }
}