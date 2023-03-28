package ru.gas.humblr.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.gas.humblr.R

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {


        findNavController().navigate(R.id.action_main_to_home)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

}