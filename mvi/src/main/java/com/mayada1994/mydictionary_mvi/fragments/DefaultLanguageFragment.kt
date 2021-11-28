package com.mayada1994.mydictionary_mvi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.activities.MainActivity
import com.mayada1994.mydictionary_mvi.adapters.DefaultLanguageListAdapter
import com.mayada1994.mydictionary_mvi.databinding.FragmentDefaultLanguageBinding
import com.mayada1994.mydictionary_mvi.di.DictionaryComponent
import com.mayada1994.mydictionary_mvi.entities.Language
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.interactors.DefaultLanguageInteractor
import com.mayada1994.mydictionary_mvi.items.DefaultLanguageItem
import com.mayada1994.mydictionary_mvi.presenters.DefaultLanguagePresenter
import com.mayada1994.mydictionary_mvi.states.DefaultLanguageState
import com.mayada1994.mydictionary_mvi.views.DefaultLanguageView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class DefaultLanguageFragment : Fragment(), DefaultLanguageView {

    private lateinit var binding: FragmentDefaultLanguageBinding

    private lateinit var presenter: DefaultLanguagePresenter

    private val defaultLanguageSubject: PublishSubject<DefaultLanguageItem> = PublishSubject.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDefaultLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = DefaultLanguagePresenter(DefaultLanguageInteractor(DictionaryComponent.languageRepository, DictionaryComponent.cacheUtils))
        presenter.bind(this)
    }

    override fun render(state: DefaultLanguageState) {
        when(state) {
            is DefaultLanguageState.DataState -> renderDataState(state.defaultLanguage, state.languages, state.isVisible)

            is DefaultLanguageState.ToolbarState -> setToolbar(state.defaultLanguage)

            is DefaultLanguageState.NavigateToAddLanguagesFragmentState -> navigateToAddLanguagesFragment(state.languages)

            is DefaultLanguageState.LoadingState -> showProgress(true)

            is DefaultLanguageState.ErrorState -> renderErrorState(state.defaultLanguage, state.resId)
        }
    }

    override fun displayLanguagesIntent(): Observable<Unit> = Observable.just(Unit)

    override fun addButtonClickIntent(): Observable<Unit> {
        return Observable.create { emitter ->
            binding.btnAdd.setOnClickListener { emitter.onNext(Unit) }
        }
    }

    override fun setDefaultLanguageIntent(): Observable<DefaultLanguageItem> = defaultLanguageSubject

    private fun renderDataState(defaultLanguage: LanguageInfo, languages: List<DefaultLanguageItem>, isVisible: Boolean) {
        showProgress(false)
        setToolbar(defaultLanguage)
        setLanguages(languages)
        changeAddButtonVisibility(isVisible)
    }

    private fun setToolbar(defaultLanguage: LanguageInfo) {
        with(binding) {
            toolbar.imgDefaultFlag.setImageResource(defaultLanguage.imageRes)
            toolbar.txtDefaultLanguage.text = getString(defaultLanguage.nameRes)
        }
    }

    private fun setLanguages(languages: List<DefaultLanguageItem>) {
        binding.languagesRecyclerView.adapter = DefaultLanguageListAdapter(
            languages,
            object : DefaultLanguageListAdapter.OnLanguageItemClickListener {
                override fun onClick(language: DefaultLanguageItem) {
                    (binding.languagesRecyclerView.adapter as DefaultLanguageListAdapter).setDefault(language)
                    defaultLanguageSubject.onNext(language)
                }
            })
    }

    private fun navigateToAddLanguagesFragment(languages: List<Language>) {
        parentFragmentManager.commit {
            replace(
                R.id.container,
                AddLanguagesFragment.newInstance(languages),
                AddLanguagesFragment::class.java.simpleName
            )
            addToBackStack(null)
        }
    }

    private fun changeAddButtonVisibility(isVisible: Boolean) {
        binding.btnAdd.isVisible = isVisible
    }

    private fun renderErrorState(defaultLanguage: LanguageInfo, resId: Int) {
        showProgress(false)
        setToolbar(defaultLanguage)
        showMessage(resId)
    }

    private fun showProgress(isVisible: Boolean) {
        (requireActivity() as MainActivity).showProgress(isVisible)
    }

    private fun showMessage(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbind()
    }

}