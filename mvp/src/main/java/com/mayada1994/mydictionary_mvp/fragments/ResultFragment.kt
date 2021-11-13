package com.mayada1994.mydictionary_mvp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mayada1994.mydictionary_mvp.contracts.ResultContract
import com.mayada1994.mydictionary_mvp.databinding.FragmentResultBinding
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.presenters.ResultPresenter

class ResultFragment : Fragment(), ResultContract.ViewInterface {

    private lateinit var binding: FragmentResultBinding

    private var result: String? = null

    private lateinit var presenter: ResultPresenter

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

        presenter = ResultPresenter(this)
        presenter.init()

        setResult()
    }

    override fun setToolbar(defaultLanguage: LanguageInfo) {
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