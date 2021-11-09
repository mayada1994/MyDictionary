package com.mayada1994.mydictionary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mayada1994.mydictionary.databinding.FragmentResultBinding
import com.mayada1994.mydictionary.utils.LanguageUtils

class ResultFragment : Fragment() {

    private lateinit var binding: FragmentResultBinding

    private var result: String? = null

    private var defaultLanguage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            result = it.getString(RESULT)
            defaultLanguage = it.getString(DEFAULT_LANGUAGE)
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

        setToolbar()
        setResult()
    }

    private fun setToolbar() {
        defaultLanguage?.let {
            LanguageUtils.getLanguageByCode(it)?.let {
                with(binding) {
                    toolbar.imgDefaultFlag.setImageResource(it.imageRes)
                    toolbar.txtDefaultLanguage.text = getString(it.nameRes)
                }
            }
        }
    }

    private fun setResult() {
        result?.let {
            binding.txtResult.text = it
        }
    }

    companion object {
        private const val RESULT = "RESULT"
        private const val DEFAULT_LANGUAGE = "DEFAULT_LANGUAGE"

        @JvmStatic
        fun newInstance(result: String, defaultLanguage: String) =
            ResultFragment().apply {
                arguments = Bundle().apply {
                    putString(RESULT, result)
                    putString(DEFAULT_LANGUAGE, defaultLanguage)
                }
            }
    }
}