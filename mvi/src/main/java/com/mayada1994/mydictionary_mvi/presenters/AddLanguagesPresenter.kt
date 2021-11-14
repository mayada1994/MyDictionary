package com.mayada1994.mydictionary_mvi.presenters

import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.interactors.AddLanguagesInteractor
import com.mayada1994.mydictionary_mvi.states.AddLanguagesState
import com.mayada1994.mydictionary_mvi.views.AddLanguagesView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class AddLanguagesPresenter(private val addLanguagesInteractor: AddLanguagesInteractor) {
    private val compositeDisposable = CompositeDisposable()
    private lateinit var view: AddLanguagesView

    private var initialScreen: Boolean = true

    private val selectedLanguages: ArrayList<LanguageInfo> = arrayListOf()

    fun bind(view: AddLanguagesView) {
        this.view = view
        compositeDisposable.add(observeDisplayLanguagesIntent())
        compositeDisposable.add(observeSaveButtonClickIntent())
        compositeDisposable.add(observeSelectLanguagesIntent())
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private fun observeDisplayLanguagesIntent() = view.displayLanguagesIntent()
        .doOnNext {
            Timber.d("Intent: display languages")
            initialScreen = it.isEmpty()
        }
        .flatMap { addLanguagesInteractor.getLanguages(it) }
        .startWith(AddLanguagesState.LoadingState)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

    private fun observeSaveButtonClickIntent() = view.saveButtonClickIntent()
        .doOnNext { Timber.d("Intent: onSaveButtonClick") }
        .flatMap { addLanguagesInteractor.onSaveButtonClick(selectedLanguages, initialScreen) }
        .startWith(AddLanguagesState.LoadingState)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

    private fun observeSelectLanguagesIntent() = view.selectLanguagesIntent()
        .doOnNext {
            Timber.d("Intent: onLanguagesSelected")
            this.selectedLanguages.clear()
            this.selectedLanguages.addAll(it)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe()

}