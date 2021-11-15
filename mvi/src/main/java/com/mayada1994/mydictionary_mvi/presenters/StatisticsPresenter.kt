package com.mayada1994.mydictionary_mvi.presenters

import com.mayada1994.mydictionary_mvi.interactors.StatisticsInteractor
import com.mayada1994.mydictionary_mvi.states.StatisticsState
import com.mayada1994.mydictionary_mvi.views.StatisticsView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class StatisticsPresenter(private val statisticsInteractor: StatisticsInteractor) {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var view: StatisticsView

    fun bind(view: StatisticsView) {
        this.view = view
        compositeDisposable.add(observeDisplayStatisticsIntent())
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private fun observeDisplayStatisticsIntent() = view.displayStatisticsIntent()
        .doOnNext {
            Timber.d("Intent: display stats")
            StatisticsState.LoadingState
        }
        .flatMap { statisticsInteractor.getData() }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

}