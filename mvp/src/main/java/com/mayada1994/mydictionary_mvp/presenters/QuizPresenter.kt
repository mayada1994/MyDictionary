package com.mayada1994.mydictionary_mvp.presenters

import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.contracts.QuizContract
import com.mayada1994.mydictionary_mvp.di.DictionaryComponent
import com.mayada1994.mydictionary_mvp.entities.Statistics
import com.mayada1994.mydictionary_mvp.entities.Word
import com.mayada1994.mydictionary_mvp.items.QuestionItem
import com.mayada1994.mydictionary_mvp.models.StatisticsDataSource
import com.mayada1994.mydictionary_mvp.models.WordDataSource
import com.mayada1994.mydictionary_mvp.utils.LanguageUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class QuizPresenter(
    private val viewInterface: QuizContract.ViewInterface,
    private val wordDataSource: WordDataSource,
    private val statisticsDataSource: StatisticsDataSource
): QuizContract.PresenterInterface {

    private val compositeDisposable = CompositeDisposable()

    private var defaultLanguage: String? = null

    companion object {
        private const val MIN_WORD_AMOUNT = 4
        private const val MAX_WORD_AMOUNT_PER_QUIZ = 20
        private const val WRONG_ANSWERS_AMOUNT_PER_QUIZ = 3
    }

    override fun init() {
        DictionaryComponent.cacheUtils.defaultLanguage?.let {
            defaultLanguage = it
            getWords(it)
            LanguageUtils.getLanguageByCode(it)?.let {
                viewInterface.setToolbar(it)
            }
        }
    }

    private fun getWords(defaultLanguage: String) {
        viewInterface.showProgress(true)
        compositeDisposable.add(
            wordDataSource.getWordsByLanguage(defaultLanguage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { viewInterface.showProgress(false) }
                .subscribeWith(object : DisposableSingleObserver<List<Word>>() {
                    override fun onSuccess(words: List<Word>) {
                        if (words.size >= MIN_WORD_AMOUNT) {
                            viewInterface.showPlaceholder(false)
                            generateQuestions(words)
                        } else {
                            viewInterface.showPlaceholder(true)
                        }
                    }

                    override fun onError(e: Throwable) {
                        viewInterface.showPlaceholder(true)
                        viewInterface.showMessage(R.string.general_error)
                    }
                })
        )
    }

    private fun generateQuestions(words: List<Word>) {
        val questionItems = arrayListOf<QuestionItem>()

        words.shuffled().take(MAX_WORD_AMOUNT_PER_QUIZ).forEach { questionWord ->
            words.filterNot { it == questionWord }.shuffled().take(WRONG_ANSWERS_AMOUNT_PER_QUIZ)
                .map { it.translation }.let { wrongAnswers ->
                    questionItems.add(
                        QuestionItem(
                            questionWord,
                            arrayListOf(questionWord.translation).apply {
                                addAll(wrongAnswers)
                                shuffle()
                            })
                    )
                }
        }

        viewInterface.setQuestions(questionItems)
    }

    override fun getResult(questions: List<QuestionItem>?) {
        if (questions.isNullOrEmpty()) {
            viewInterface.showMessage(R.string.general_error)
            return
        }

        defaultLanguage?.let { language ->
            addResultToStats(
                Statistics(
                    result = "${questions.filter { it.word.translation == it.selectedAnswer }.size}/${questions.size}",
                    timestamp = System.currentTimeMillis(),
                    language = language
                )
            )
        }
    }

    private fun addResultToStats(result: Statistics) {
        viewInterface.showProgress(true)
        compositeDisposable.add(
            statisticsDataSource.insertStatistics(result)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    viewInterface.showProgress(false)
                    viewInterface.showResultFragment(result.result)
                }
                .subscribeWith(object : DisposableCompletableObserver(){
                    override fun onComplete() {
                        Timber.d("Stat added successfully")
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e)
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