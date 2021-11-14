package com.mayada1994.mydictionary_mvi.presenters

import com.mayada1994.mydictionary_mvi.interactors.MainMenuInteractor
import com.mayada1994.mydictionary_mvi.views.MainMenuView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MainMenuPresenter(private val mainMenuInteractor: MainMenuInteractor) {
    private val compositeDisposable = CompositeDisposable()
    private lateinit var view: MainMenuView

    fun bind(view: MainMenuView) {
        this.view = view
        compositeDisposable.add(observeSelectMenuItemIntent())
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private fun observeSelectMenuItemIntent() = view.selectMenuItemIntent()
        .doOnNext { Timber.d("Intent: select menu item $it") }
        .flatMap { mainMenuInteractor.getSelectedMenuItem(it) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

}