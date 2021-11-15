package com.mayada1994.mydictionary_mvi.presenters

import com.mayada1994.mydictionary_mvi.interactors.QuizInteractor
import com.mayada1994.mydictionary_mvi.states.QuizState
import com.mayada1994.mydictionary_mvi.views.QuizView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class QuizPresenter(private val quizInteractor: QuizInteractor) {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var view: QuizView

    fun bind(view: QuizView) {
        this.view = view
        compositeDisposable.add(observeDisplayQuestionsIntent())
        compositeDisposable.add(observeDisplayResultIntent())
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private fun observeDisplayQuestionsIntent() = view.displayQuestionsIntent()
        .doOnNext {
            Timber.d("Intent: display questions")
            QuizState.LoadingState
        }
        .flatMap { quizInteractor.getData() }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

    private fun observeDisplayResultIntent() = view.displayResultIntent()
        .doOnNext {
            Timber.d("Intent: display result")
            QuizState.LoadingState
        }
        .observeOn(Schedulers.io())
        .flatMap { quizInteractor.getResult(it) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

}