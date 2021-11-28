package com.mayada1994.mydictionary_mvi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mayada1994.mydictionary_mvi.databinding.FragmentResultBinding
import com.mayada1994.mydictionary_mvi.di.DictionaryComponent
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.interactors.ResultInteractor
import com.mayada1994.mydictionary_mvi.presenters.ResultPresenter
import com.mayada1994.mydictionary_mvi.states.ResultState
import com.mayada1994.mydictionary_mvi.views.ResultView
import io.reactivex.Observable

class ResultFragment : Fragment(), ResultView {

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

        presenter = ResultPresenter(ResultInteractor(DictionaryComponent.cacheUtils))
        presenter.bind(this)

        setResult()
    }

    override fun render(state: ResultState) {
        when(state) {
            is ResultState.DataState -> setToolbar(state.defaultLanguage)

            is ResultState.ErrorState -> showToast(state.resId)
        }
    }

    override fun displayDataIntent(): Observable<Unit> = Observable.just(Unit)

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

    private fun showToast(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbind()
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