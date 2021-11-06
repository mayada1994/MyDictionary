package com.mayada1994.mydictionary.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mayada1994.mydictionary.R
import com.mayada1994.mydictionary.databinding.FragmentAddLanguagesBinding

class AddLanguagesFragment : Fragment() {

    private lateinit var binding: FragmentAddLanguagesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddLanguagesBinding.inflate(inflater, container, false)
        return binding.root
    }

}