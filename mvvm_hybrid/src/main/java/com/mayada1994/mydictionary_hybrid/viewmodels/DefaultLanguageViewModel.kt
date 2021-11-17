package com.mayada1994.mydictionary_hybrid.viewmodels

import com.mayada1994.mydictionary_hybrid.R
import com.mayada1994.mydictionary_hybrid.di.DictionaryComponent
import com.mayada1994.mydictionary_hybrid.entities.Language
import com.mayada1994.mydictionary_hybrid.items.DefaultLanguageItem
import com.mayada1994.mydictionary_hybrid.repositories.LanguageRepository
import com.mayada1994.mydictionary_hybrid.utils.LanguageUtils
import com.mayada1994.mydictionary_hybrid.utils.ViewEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class DefaultLanguageViewModel(private val languageRepository: LanguageRepository) : BaseViewModel() {

    sealed class DefaultLanguageEvent {
        data class SetLanguages(val languages: List<DefaultLanguageItem>) : ViewEvent

        data class SetAddButtonVisibility(val isVisible: Boolean) : ViewEvent

        data class NavigateToAddLanguagesFragment(val languages: List<Language>) : ViewEvent
    }

    private var currentLanguages: List<Language> = emptyList()

    private val compositeDisposable = CompositeDisposable()

    private var defaultLanguage: String? = null

    fun init() {
        DictionaryComponent.cacheUtils.defaultLanguage?.let {
            defaultLanguage = it
            getLanguages()
            LanguageUtils.getLanguageByCode(it)?.let { setEvent(BaseEvent.SetDefaultLanguage(it)) }
        }
    }

    private fun getLanguages() {
        setEvent(BaseEvent.ShowProgress(true))
        compositeDisposable.add(
            languageRepository.getLanguages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { setEvent(BaseEvent.ShowProgress(false)) }
                .subscribeWith(object : DisposableSingleObserver<List<Language>>() {
                    override fun onSuccess(languages: List<Language>) {
                        currentLanguages = languages
                        generateDefaultLanguageItems(languages)
                        if (languages.size == LanguageUtils.languagesTotal) {
                            setEvent(DefaultLanguageEvent.SetAddButtonVisibility(false))
                        }
                    }

                    override fun onError(e: Throwable) {
                        setEvent(BaseEvent.ShowMessage(R.string.general_error))
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
        setEvent(DefaultLanguageEvent.SetLanguages(languageItems))
    }

    fun onAddButtonClick() {
        setEvent(DefaultLanguageEvent.NavigateToAddLanguagesFragment(currentLanguages))
    }

    fun setDefaultLanguage(language: DefaultLanguageItem) {
        DictionaryComponent.cacheUtils.defaultLanguage = language.locale
        LanguageUtils.getLanguageByCode(language.locale)?.let {
            defaultLanguage = it.locale
            setEvent(BaseEvent.SetDefaultLanguage(it))
        }
    }

    fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

}