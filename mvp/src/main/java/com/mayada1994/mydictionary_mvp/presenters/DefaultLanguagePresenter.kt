package com.mayada1994.mydictionary_mvp.presenters

import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.contracts.DefaultLanguageContract
import com.mayada1994.mydictionary_mvp.entities.Language
import com.mayada1994.mydictionary_mvp.items.DefaultLanguageItem
import com.mayada1994.mydictionary_mvp.models.LanguageDataSource
import com.mayada1994.mydictionary_mvp.utils.CacheUtils
import com.mayada1994.mydictionary_mvp.utils.LanguageUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class DefaultLanguagePresenter(
    private val viewInterface: DefaultLanguageContract.ViewInterface,
    private val languageDataSource: LanguageDataSource,
    private val cacheUtils: CacheUtils
): DefaultLanguageContract.PresenterInterface {

    private var defaultLanguage: String? = null

    private var currentLanguages: List<Language> = emptyList()

    private val compositeDisposable = CompositeDisposable()

    override fun init() {
        cacheUtils.defaultLanguage?.let {
            defaultLanguage = it
            getLanguages()
            LanguageUtils.getLanguageByCode(it)?.let { viewInterface.setToolbar(it) }
        }
    }

    private fun getLanguages() {
        viewInterface.showProgress(true)
        compositeDisposable.add(
            languageDataSource.getLanguages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { viewInterface.showProgress(false) }
                .subscribeWith(object : DisposableSingleObserver<List<Language>>() {
                    override fun onSuccess(languages: List<Language>) {
                        currentLanguages = languages
                        generateDefaultLanguageItems(languages)
                        if (languages.size == LanguageUtils.languagesTotal) {
                            viewInterface.changeAddButtonVisibility(false)
                        }
                    }

                    override fun onError(e: Throwable) {
                        viewInterface.showMessage(R.string.general_error)
                    }
                })
        )
    }

    private fun generateDefaultLanguageItems(languages: List<Language>) {
        val languageItems = arrayListOf<DefaultLanguageItem>()
        languages.forEach { language ->
            LanguageUtils.getLanguageByCode(language.code)?.let { languageInfo ->
                languageItems.add(
                    languageInfo.toDefaultLanguageItem()
                        .apply { isDefault = locale == defaultLanguage })
            }
        }
        viewInterface.setLanguages(languageItems)
    }

    override fun onAddButtonClick() {
        viewInterface.navigateToAddLanguagesFragment(currentLanguages)
    }

    override fun setDefaultLanguage(language: DefaultLanguageItem) {
        cacheUtils.defaultLanguage = language.locale
        LanguageUtils.getLanguageByCode(language.locale)?.let {
            defaultLanguage = it.locale
            viewInterface.setToolbar(it)
        }
    }

    override fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

}