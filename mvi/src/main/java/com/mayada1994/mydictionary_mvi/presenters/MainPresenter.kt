package com.mayada1994.mydictionary_mvi.presenters

import com.mayada1994.mydictionary_mvi.interactors.MainInteractor
import com.mayada1994.mydictionary_mvi.views.MainView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MainPresenter(private val mainInteractor: MainInteractor) {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var view: MainView

    fun bind(view: MainView) {
        this.view = view
        compositeDisposable.add(observeDisplayInitialScreenIntent())
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private fun observeDisplayInitialScreenIntent() = view.displayInitialScreenIntent()
        .doOnNext { Timber.d("Intent: display initial screen") }
        .flatMap { mainInteractor.getInitialScreen() }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

}