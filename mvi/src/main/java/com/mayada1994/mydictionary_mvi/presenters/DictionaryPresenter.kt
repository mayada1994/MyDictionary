package com.mayada1994.mydictionary_mvi.presenters

import com.mayada1994.mydictionary_mvi.interactors.DictionaryInteractor
import com.mayada1994.mydictionary_mvi.states.DictionaryState
import com.mayada1994.mydictionary_mvi.views.DictionaryView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class DictionaryPresenter(private val dictionaryInteractor: DictionaryInteractor) {
    private val compositeDisposable = CompositeDisposable()
    private lateinit var view: DictionaryView

    fun bind(view: DictionaryView) {
        this.view = view
        compositeDisposable.add(observeDisplayWordsIntent())
        compositeDisposable.add(observeAddButtonClickIntent())
        compositeDisposable.add(observeSaveButtonClickIntent())
        compositeDisposable.add(observeDeleteButtonClickIntent())
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private fun observeDisplayWordsIntent() = view.displayWordsIntent()
        .doOnNext {
            Timber.d("Intent: display words")
            DictionaryState.LoadingState
        }
        .flatMap { dictionaryInteractor.getData() }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

    private fun observeAddButtonClickIntent() = view.addButtonClickIntent()
        .doOnNext { Timber.d("Intent: onAddButtonClick") }
        .flatMap { dictionaryInteractor.onAddButtonClick() }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

    private fun observeSaveButtonClickIntent() = view.saveButtonClickIntent()
        .doOnNext {
            Timber.d("Intent: onSaveButtonClick")
            DictionaryState.LoadingState
        }
        .observeOn(Schedulers.io())
        .flatMap { dictionaryInteractor.onSaveButtonClick(it.first, it.second) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

    private fun observeDeleteButtonClickIntent() = view.deleteButtonClickIntent()
        .doOnNext {
            Timber.d("Intent: onDeleteButtonClick")
            DictionaryState.LoadingState
        }
        .observeOn(Schedulers.io())
        .flatMap { dictionaryInteractor.deleteWordFromDictionary(it) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

}