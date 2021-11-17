package com.mayada1994.mydictionary.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mayada1994.mydictionary.R
import com.mayada1994.mydictionary.activities.MainActivity
import com.mayada1994.mydictionary.adapters.LanguagesAdapter
import com.mayada1994.mydictionary.databinding.FragmentAddLanguagesBinding
import com.mayada1994.mydictionary.di.DictionaryComponent
import com.mayada1994.mydictionary.entities.Language
import com.mayada1994.mydictionary.entities.LanguageInfo
import com.mayada1994.mydictionary.models.LanguageDataSource
import com.mayada1994.mydictionary.utils.LanguageUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers

class AddLanguagesFragment : Fragment() {

    private lateinit var binding: FragmentAddLanguagesBinding

    private var languages: List<LanguageInfo>? = null
    private var initialScreen: Boolean = true

    private lateinit var languageDataSource: LanguageDataSource

    private val compositeDisposable = CompositeDisposable()

    private val selectedLanguages: ArrayList<LanguageInfo> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getParcelableArrayList<Language>(LANGUAGES)?.let { usedLanguages ->
            languages = LanguageUtils.getLanguages()
                .filterNot { it.locale in usedLanguages.map { it.code } }
            initialScreen = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddLanguagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        languageDataSource = DictionaryComponent.languageDataSource

        initListeners()
        setLanguages()
    }

    private fun initListeners() {
        binding.btnSave.setOnClickListener {
            if (selectedLanguages.isNullOrEmpty()) {
                showToast(R.string.pick_languages_warning)
            } else {
                saveLanguages(selectedLanguages)
            }
        }
    }

    private fun saveLanguages(languages: List<LanguageInfo>) {
        (requireActivity() as MainActivity).showProgress(true)
        compositeDisposable.add(
            languageDataSource.insertLanguages(languages.map { it.toLanguage() })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { (requireActivity() as MainActivity).showProgress(false) }
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        if (initialScreen) {
                            DictionaryComponent.cacheUtils.defaultLanguage = languages[0].locale
                            (requireActivity() as MainActivity).setFragment(MainFragment::class.java)
                        } else {
                            activity?.onBackPressed()
                        }
                    }

                    override fun onError(e: Throwable) {
                        showToast(R.string.general_error)
                    }
                })
        )
    }

    private fun setLanguages() {
        binding.languagesRecyclerView.adapter =
            LanguagesAdapter(
                languages?.map { it.toLanguageItem() } ?: LanguageUtils.getLanguages()
                    .map { it.toLanguageItem() },
                object : LanguagesAdapter.OnLanguageItemClickListener {
                    override fun onClick(languages: List<LanguageInfo>) {
                        selectedLanguages.clear()
                        selectedLanguages.addAll(languages)
                    }
                })
    }

    private fun showToast(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
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