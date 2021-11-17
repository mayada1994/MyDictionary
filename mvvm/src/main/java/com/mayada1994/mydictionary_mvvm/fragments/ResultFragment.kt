package com.mayada1994.mydictionary_mvvm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mayada1994.mydictionary_mvvm.databinding.FragmentResultBinding
import com.mayada1994.mydictionary_mvvm.di.DictionaryComponent
import com.mayada1994.mydictionary_mvvm.entities.LanguageInfo
import com.mayada1994.mydictionary_mvvm.viewmodels.ResultViewModel

class ResultFragment : Fragment() {

    private lateinit var binding: FragmentResultBinding

    private var result: String? = null

    private val viewModel by viewModels<ResultViewModel> { DictionaryComponent.viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            result = it.getString(RESULT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()

        viewModel.init()

        setResult()
    }

    private fun initObservers() {
        viewModel.defaultLanguage.observe(viewLifecycleOwner, { defaultLanguage ->
            setToolbar(defaultLanguage)
        })
    }

    private fun setToolbar(defaultLanguage: LanguageInfo) {
        with(binding) {
            toolbar.imgDefaultFlag.setImageResource(defaultLanguage.imageRes)
            toolbar.txtDefaultLanguage.text = getString(defaultLanguage.nameRes)
        }
    }

    private fun setResult() {
        result?.let {
            binding.txtResult.text = it
        }
    }

    companion object {
        private const val RESULT = "RESULT"

        @JvmStatic
        fun newInstance(result: String) =
            ResultFragment().apply {
                arguments = Bundle().apply {
                    putString(RESULT, result)
                }
            }
    }
}