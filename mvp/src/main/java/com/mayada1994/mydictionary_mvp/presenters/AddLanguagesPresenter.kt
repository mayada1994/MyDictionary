package com.mayada1994.mydictionary_mvp.presenters

import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.contracts.AddLanguagesContract
import com.mayada1994.mydictionary_mvp.di.DictionaryComponent
import com.mayada1994.mydictionary_mvp.entities.Language
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.models.LanguageDataSource
import com.mayada1994.mydictionary_mvp.utils.LanguageUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers

class AddLanguagesPresenter(
    private val viewInterface: AddLanguagesContract.ViewInterface,
    private val languageDataSource: LanguageDataSource
): AddLanguagesContract.PresenterInterface {

    private var initialScreen: Boolean = true

    private val compositeDisposable = CompositeDisposable()

    private val selectedLanguages: ArrayList<LanguageInfo> = arrayListOf()

    override fun init(usedLanguages: List<Language>) {
        viewInterface.setLanguages(
            LanguageUtils.getLanguages()
                .filterNot { it.locale in usedLanguages.map { it.code } }
                .map { it.toLanguageItem() }
        )
        initialScreen = usedLanguages.isEmpty()
    }

    override fun onLanguagesSelected(selectedLanguages: List<LanguageInfo>) {
        this.selectedLanguages.clear()
        this.selectedLanguages.addAll(selectedLanguages)
    }

    override fun onSaveButtonClick() {
        if (selectedLanguages.isNullOrEmpty()) {
            viewInterface.showMessage(R.string.pick_languages_warning)
        } else {
            saveLanguages(selectedLanguages)
        }
    }

    override fun saveLanguages(languages: List<LanguageInfo>) {
        viewInterface.showProgress(true)
        compositeDisposable.add(
            languageDataSource.insertLanguages(languages.map { it.toLanguage() })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { viewInterface.showProgress(false) }
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        if (initialScreen) {
                            DictionaryComponent.cacheUtils.defaultLanguage = languages[0].locale
//                           viewInterface.setFragment(MainFragment::class.java)
                        } else {
                            viewInterface.onBackPressed()
                        }
                    }

                    override fun onError(e: Throwable) {
                        viewInterface.showMessage(R.string.general_error)
                    }
                })
        )
    }

    override fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

}