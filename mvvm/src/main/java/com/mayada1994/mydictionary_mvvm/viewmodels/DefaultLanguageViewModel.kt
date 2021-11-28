package com.mayada1994.mydictionary_mvvm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mayada1994.mydictionary_mvvm.R
import com.mayada1994.mydictionary_mvvm.entities.Language
import com.mayada1994.mydictionary_mvvm.entities.LanguageInfo
import com.mayada1994.mydictionary_mvvm.items.DefaultLanguageItem
import com.mayada1994.mydictionary_mvvm.repositories.LanguageRepository
import com.mayada1994.mydictionary_mvvm.utils.CacheUtils
import com.mayada1994.mydictionary_mvvm.utils.LanguageUtils
import com.mayada1994.mydictionary_mvvm.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class DefaultLanguageViewModel(
    private val languageRepository: LanguageRepository,
    private val cacheUtils: CacheUtils
) : ViewModel() {

    private var currentLanguages: List<Language> = emptyList()

    private val compositeDisposable = CompositeDisposable()

    private val _languagesList = SingleLiveEvent<List<DefaultLanguageItem>>()
    val languagesList: LiveData<List<DefaultLanguageItem>>
        get() = _languagesList

    private val _defaultLanguage = SingleLiveEvent<LanguageInfo>()
    val defaultLanguage: LiveData<LanguageInfo>
        get() = _defaultLanguage

    private val _addButtonVisibility = SingleLiveEvent<Boolean>()
    val addButtonVisibility: LiveData<Boolean>
        get() = _addButtonVisibility

    private val _navigateToAddLanguagesFragment = SingleLiveEvent<List<Language>>()
    val navigateToAddLanguagesFragment: LiveData<List<Language>>
        get() = _navigateToAddLanguagesFragment

    private val _isProgressVisible = SingleLiveEvent<Boolean>()
    val isProgressVisible: LiveData<Boolean>
        get() = _isProgressVisible

    private val _toastMessageStringResId = SingleLiveEvent<Int>()
    val toastMessageStringResId: LiveData<Int>
        get() = _toastMessageStringResId

    fun init() {
        cacheUtils.defaultLanguage?.let {
            LanguageUtils.getLanguageByCode(it)?.let { _defaultLanguage.postValue(it) }
            getLanguages()
        }
    }

    private fun getLanguages() {
        _isProgressVisible.postValue(true)
        compositeDisposable.add(
            languageRepository.getLanguages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { _isProgressVisible.postValue(false) }
                .subscribeWith(object : DisposableSingleObserver<List<Language>>() {
                    override fun onSuccess(languages: List<Language>) {
                        currentLanguages = languages
                        generateDefaultLanguageItems(languages)
                        if (languages.size == LanguageUtils.languagesTotal) {
                            _addButtonVisibility.postValue(false)
                        }
                    }

                    override fun onError(e: Throwable) {
                        _toastMessageStringResId.postValue(R.string.general_error)
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
                        .apply { isDefault = locale == defaultLanguage.value?.locale })
            }
        }
        _languagesList.postValue(languageItems)
    }

    fun onAddButtonClick() {
        _navigateToAddLanguagesFragment.postValue(currentLanguages)
    }

    fun setDefaultLanguage(language: DefaultLanguageItem) {
        cacheUtils.defaultLanguage = language.locale
        LanguageUtils.getLanguageByCode(language.locale)?.let {
            _defaultLanguage.postValue(it)
        }
    }

    fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

}