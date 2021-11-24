package com.mayada1994.mydictionary_mvi.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mayada1994.mydictionary_mvi.activities.MainActivity
import com.mayada1994.mydictionary_mvi.adapters.LanguagesAdapter
import com.mayada1994.mydictionary_mvi.databinding.FragmentAddLanguagesBinding
import com.mayada1994.mydictionary_mvi.di.DictionaryComponent
import com.mayada1994.mydictionary_mvi.entities.Language
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.interactors.AddLanguagesInteractor
import com.mayada1994.mydictionary_mvi.items.LanguageItem
import com.mayada1994.mydictionary_mvi.presenters.AddLanguagesPresenter
import com.mayada1994.mydictionary_mvi.states.AddLanguagesState
import com.mayada1994.mydictionary_mvi.states.AddLanguagesState.*
import com.mayada1994.mydictionary_mvi.views.AddLanguagesView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class AddLanguagesFragment : Fragment(), AddLanguagesView {

    private lateinit var binding: FragmentAddLanguagesBinding

    private lateinit var presenter: AddLanguagesPresenter

    private val selectedLanguagesSubject: PublishSubject<List<LanguageInfo>> = PublishSubject.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddLanguagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = AddLanguagesPresenter(AddLanguagesInteractor(DictionaryComponent.languageRepository, DictionaryComponent.cacheUtils))
        presenter.bind(this)
    }

    override fun render(state: AddLanguagesState) {
        when(state) {
            is DataState -> renderDataState(state.languages)

            is ScreenState -> renderScreenState(state.fragmentClass)

            is LoadingState -> renderLoadingState()

            is BackPressedState -> renderBackPressedState()

            is CompletedState -> renderCompletedState(state.resId)
        }
    }

    override fun displayLanguagesIntent(): Observable<List<Language>> = Observable.just(arguments?.getParcelableArrayList(LANGUAGES) ?: emptyList())

    override fun saveButtonClickIntent(): Observable<Unit> {
        return Observable.create { emitter ->
            binding.btnSave.setOnClickListener {
                emitter.onNext(Unit)
            }
        }
    }

    override fun selectLanguagesIntent(): Observable<List<LanguageInfo>> = selectedLanguagesSubject

    private fun renderDataState(languages: List<LanguageItem>) {
        showProgress(false)

        binding.languagesRecyclerView.adapter = LanguagesAdapter(
            languages,
            object : LanguagesAdapter.OnLanguageItemClickListener {
                override fun onClick(languages: List<LanguageInfo>) {
                    selectedLanguagesSubject.onNext(languages)
                }
            })
    }

    private fun renderScreenState(fragmentClass: Class<out Fragment>) {
        (requireActivity() as MainActivity).setFragment(fragmentClass)
    }

    private fun renderBackPressedState() {
        activity?.onBackPressed()
    }

    private fun renderLoadingState() {
        showProgress(true)
    }

    private fun renderCompletedState(resId: Int) {
        showProgress(false)
        showToast(resId)
    }

    private fun showProgress(isVisible: Boolean) {
        (requireActivity() as MainActivity).showProgress(isVisible)
    }

    private fun showToast(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        presenter.unbind()
    }

    companion object {
        private const val LANGUAGES = "LANGUAGES"

        @JvmStatic
        fun newInstance(languages: List<Language>?) =
            AddLanguagesFragment().apply {
                arguments = Bundle().apply {
                    languages?.let {
                        putParcelableArrayList(LANGUAGES, it as ArrayList<out Parcelable>)
                    }
                }
            }
    }

}