package com.mayada1994.mydictionary_mvc.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mayada1994.mydictionary_mvc.R
import com.mayada1994.mydictionary_mvc.activities.MainActivity
import com.mayada1994.mydictionary_mvc.adapters.DefaultLanguageListAdapter
import com.mayada1994.mydictionary_mvc.databinding.FragmentDefaultLanguageBinding
import com.mayada1994.mydictionary_mvc.di.DictionaryComponent
import com.mayada1994.mydictionary_mvc.entities.Language
import com.mayada1994.mydictionary_mvc.items.DefaultLanguageItem
import com.mayada1994.mydictionary_mvc.models.LanguageDataSource
import com.mayada1994.mydictionary_mvc.utils.LanguageUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class DefaultLanguageFragment : Fragment() {

    private lateinit var binding: FragmentDefaultLanguageBinding

    private lateinit var languageDataSource: LanguageDataSource

    private var defaultLanguage: String? = null

    private var currentLanguages: List<Language> = emptyList()

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDefaultLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        languageDataSource = DictionaryComponent.languageDataSource

        initListeners()
    }

    override fun onResume() {
        super.onResume()

        setToolbar()
        getLanguages()
    }

    private fun initListeners() {
        binding.btnAdd.setOnClickListener { goToAddLanguagesFragment() }
    }

    private fun setToolbar() {
        DictionaryComponent.cacheUtils.defaultLanguage?.let {
            defaultLanguage = it
            LanguageUtils.getLanguageByCode(it)?.let {
                with(binding) {
                    toolbar.imgDefaultFlag.setImageResource(it.imageRes)
                    toolbar.txtDefaultLanguage.text = getString(it.nameRes)
                }
            }
        }
    }

    private fun getLanguages() {
        (requireActivity() as MainActivity).showProgress(true)
        compositeDisposable.add(
            languageDataSource.getLanguages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { (requireActivity() as MainActivity).showProgress(false) }
                .subscribeWith(object : DisposableSingleObserver<List<Language>>() {
                    override fun onSuccess(languages: List<Language>) {
                        currentLanguages = languages
                        setLanguages(languages)
                        if (languages.size == LanguageUtils.languagesTotal) {
                            binding.btnAdd.isVisible = false
                        }
                    }

                    override fun onError(e: Throwable) {
                        showToast(R.string.general_error)
                    }
                })
        )
    }

    private fun setLanguages(languages: List<Language>) {
        val languageItems = arrayListOf<DefaultLanguageItem>()
        languages.forEach { language ->
            LanguageUtils.getLanguageByCode(language.code)?.let { languageInfo ->
                languageItems.add(
                    languageInfo.toDefaultLanguageItem()
                        .apply { isDefault = locale == defaultLanguage })
            }
        }
        binding.languagesRecyclerView.adapter = DefaultLanguageListAdapter(
            languageItems,
            object : DefaultLanguageListAdapter.OnLanguageItemClickListener {
                override fun onClick(language: DefaultLanguageItem) {
                    (binding.languagesRecyclerView.adapter as DefaultLanguageListAdapter).setDefault(language)
                    DictionaryComponent.cacheUtils.defaultLanguage = language.locale
                    setToolbar()
                }
            })
    }

    private fun goToAddLanguagesFragment() {
        parentFragmentManager.commit {
            replace(
                R.id.container,
                AddLanguagesFragment.newInstance(currentLanguages),
                AddLanguagesFragment::class.java.simpleName
            )
            addToBackStack(null)
        }
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

}