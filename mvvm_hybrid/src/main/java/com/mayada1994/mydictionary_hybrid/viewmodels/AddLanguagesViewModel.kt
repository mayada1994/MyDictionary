package com.mayada1994.mydictionary_hybrid.viewmodels

import androidx.fragment.app.Fragment
import com.mayada1994.mydictionary_hybrid.R
import com.mayada1994.mydictionary_hybrid.di.DictionaryComponent
import com.mayada1994.mydictionary_hybrid.entities.Language
import com.mayada1994.mydictionary_hybrid.entities.LanguageInfo
import com.mayada1994.mydictionary_hybrid.fragments.MainFragment
import com.mayada1994.mydictionary_hybrid.items.LanguageItem
import com.mayada1994.mydictionary_hybrid.repositories.LanguageRepository
import com.mayada1994.mydictionary_hybrid.utils.LanguageUtils
import com.mayada1994.mydictionary_hybrid.utils.ViewEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers

class AddLanguagesViewModel(private val languageRepository: LanguageRepository): BaseViewModel() {

    private var initialScreen: Boolean = true

    private val compositeDisposable = CompositeDisposable()

    private val selectedLanguages: ArrayList<LanguageInfo> = arrayListOf()

    sealed class AddLanguagesEvent {
        data class SetLanguages(val languages: List<LanguageItem>) : ViewEvent

        data class ShowSelectedScreen(val fragmentClass: Class<out Fragment>) : ViewEvent

        object OnBackPressed: ViewEvent
    }

    fun init(usedLanguages: List<Language>) {
        setEvent(AddLanguagesEvent.SetLanguages(
            LanguageUtils.getLanguages()
                .filterNot { it.locale in usedLanguages.map { it.code } }
                .map { it.toLanguageItem() }
        ))
        initialScreen = usedLanguages.isEmpty()
    }

    fun onLanguagesSelected(selectedLanguages: List<LanguageInfo>) {
        this.selectedLanguages.clear()
        this.selectedLanguages.addAll(selectedLanguages)
    }

    fun onSaveButtonClick() {
        if (selectedLanguages.isNullOrEmpty()) {
            setEvent(BaseEvent.ShowMessage(R.string.pick_languages_warning))
        } else {
            saveLanguages(selectedLanguages)
        }
    }

    private fun saveLanguages(languages: List<LanguageInfo>) {
        setEvent(BaseEvent.ShowProgress(true))
        compositeDisposable.add(
            languageRepository.insertLanguages(languages.map { it.toLanguage() })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { setEvent(BaseEvent.ShowProgress(false)) }
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        if (initialScreen) {
                            DictionaryComponent.cacheUtils.defaultLanguage = languages[0].locale
                            setEvent(AddLanguagesEvent.ShowSelectedScreen(MainFragment::class.java))
                        } else {
                            setEvent(AddLanguagesEvent.OnBackPressed)
                        }
                    }

                    override fun onError(e: Throwable) {
                        setEvent(BaseEvent.ShowMessage(R.string.general_error))
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