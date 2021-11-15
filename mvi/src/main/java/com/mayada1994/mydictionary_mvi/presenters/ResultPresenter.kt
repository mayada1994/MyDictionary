package com.mayada1994.mydictionary_mvi.presenters

import com.mayada1994.mydictionary_mvi.interactors.ResultInteractor
import com.mayada1994.mydictionary_mvi.views.ResultView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ResultPresenter(private val resultInteractor: ResultInteractor) {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var view: ResultView

    fun bind(view: ResultView) {
        this.view = view
        compositeDisposable.add(observeDisplayDataIntent())
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private fun observeDisplayDataIntent() = view.displayDataIntent()
        .doOnNext { Timber.d("Intent: display toolbar data") }
        .flatMap { resultInteractor.getData() }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

}