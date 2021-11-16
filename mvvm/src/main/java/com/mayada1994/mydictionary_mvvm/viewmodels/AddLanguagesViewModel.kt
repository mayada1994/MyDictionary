package com.mayada1994.mydictionary_mvvm.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mayada1994.mydictionary_mvvm.R
import com.mayada1994.mydictionary_mvvm.di.DictionaryComponent
import com.mayada1994.mydictionary_mvvm.entities.Language
import com.mayada1994.mydictionary_mvvm.entities.LanguageInfo
import com.mayada1994.mydictionary_mvvm.fragments.MainFragment
import com.mayada1994.mydictionary_mvvm.items.LanguageItem
import com.mayada1994.mydictionary_mvvm.repositories.LanguageRepository
import com.mayada1994.mydictionary_mvvm.utils.LanguageUtils
import com.mayada1994.mydictionary_mvvm.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers

class AddLanguagesViewModel(private val languageRepository: LanguageRepository): ViewModel() {

    private var initialScreen: Boolean = true

    private val compositeDisposable = CompositeDisposable()

    private val selectedLanguages: ArrayList<LanguageInfo> = arrayListOf()

    private val _languagesList = SingleLiveEvent<List<LanguageItem>>()
    val languagesList: LiveData<List<LanguageItem>>
        get() = _languagesList

    private val _selectedScreen = SingleLiveEvent<Class<out Fragment>>()
    val selectedScreen: LiveData<Class<out Fragment>>
        get() = _selectedScreen

    private val _onBackPressed = SingleLiveEvent<Boolean>()
    val onBackPressed: LiveData<Boolean>
        get() = _onBackPressed

    private val _isProgressVisible = SingleLiveEvent<Boolean>()
    val isProgressVisible: LiveData<Boolean>
        get() = _isProgressVisible

    private val _toastMessageStringResId = SingleLiveEvent<Int>()
    val toastMessageStringResId: LiveData<Int>
        get() = _toastMessageStringResId

    fun init(usedLanguages: List<Language>) {
        _languagesList.postValue(
            LanguageUtils.getLanguages()
                .filterNot { it.locale in usedLanguages.map { it.code } }
                .map { it.toLanguageItem() }
        )
        initialScreen = usedLanguages.isEmpty()
    }

    fun onLanguagesSelected(selectedLanguages: List<LanguageInfo>) {
        this.selectedLanguages.clear()
        this.selectedLanguages.addAll(selectedLanguages)
    }

    fun onSaveButtonClick() {
        if (selectedLanguages.isNullOrEmpty()) {
            _toastMessageStringResId.postValue(R.string.pick_languages_warning)
        } else {
            saveLanguages(selectedLanguages)
        }
    }

    private fun saveLanguages(languages: List<LanguageInfo>) {
        _isProgressVisible.postValue(true)
        compositeDisposable.add(
            languageRepository.insertLanguages(languages.map { it.toLanguage() })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { _isProgressVisible.postValue(false) }
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        if (initialScreen) {
                            DictionaryComponent.cacheUtils.defaultLanguage = languages[0].locale
                            _selectedScreen.postValue(MainFragment::class.java)
                        } else {
                            _onBackPressed.postValue(true)
                        }
                    }

                    override fun onError(e: Throwable) {
                        _toastMessageStringResId.postValue(R.string.general_error)
                    }
                })
        )
    }

    fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

}