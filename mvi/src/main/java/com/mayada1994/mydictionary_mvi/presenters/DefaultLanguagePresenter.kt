package com.mayada1994.mydictionary_mvi.presenters

import com.mayada1994.mydictionary_mvi.interactors.DefaultLanguageInteractor
import com.mayada1994.mydictionary_mvi.states.DefaultLanguageState
import com.mayada1994.mydictionary_mvi.views.DefaultLanguageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class DefaultLanguagePresenter(private val defaultLanguageInteractor: DefaultLanguageInteractor) {
    private val compositeDisposable = CompositeDisposable()
    private lateinit var view: DefaultLanguageView

    fun bind(view: DefaultLanguageView) {
        this.view = view
        compositeDisposable.add(observeDisplayLanguagesIntent())
        compositeDisposable.add(observeAddButtonClickIntent())
        compositeDisposable.add(observeSetDefaultLanguage())
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private fun observeDisplayLanguagesIntent() = view.displayLanguagesIntent()
        .doOnNext {
            Timber.d("Intent: display languages")
            DefaultLanguageState.LoadingState
        }
        .flatMap { defaultLanguageInteractor.getData() }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

    private fun observeAddButtonClickIntent() = view.addButtonClickIntent()
        .doOnNext { Timber.d("Intent: onAddButtonClick") }
        .flatMap { defaultLanguageInteractor.onAddButtonClick() }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

    private fun observeSetDefaultLanguage() = view.setDefaultLanguageIntent()
        .doOnNext { Timber.d("Intent: set default language") }
        .flatMap { defaultLanguageInteractor.setDefaultLanguage(it) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

}