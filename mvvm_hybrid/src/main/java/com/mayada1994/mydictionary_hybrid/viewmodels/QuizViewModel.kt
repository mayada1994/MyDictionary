package com.mayada1994.mydictionary_hybrid.viewmodels

import com.mayada1994.mydictionary_hybrid.R
import com.mayada1994.mydictionary_hybrid.entities.Statistics
import com.mayada1994.mydictionary_hybrid.entities.Word
import com.mayada1994.mydictionary_hybrid.items.QuestionItem
import com.mayada1994.mydictionary_hybrid.repositories.StatisticsRepository
import com.mayada1994.mydictionary_hybrid.repositories.WordRepository
import com.mayada1994.mydictionary_hybrid.utils.CacheUtils
import com.mayada1994.mydictionary_hybrid.utils.LanguageUtils
import com.mayada1994.mydictionary_hybrid.utils.ViewEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class QuizViewModel(
    private val wordRepository: WordRepository,
    private val statisticsRepository: StatisticsRepository,
    private val cacheUtils: CacheUtils
) : BaseViewModel() {

    sealed class QuizEvent {
        data class SetQuestions(val questions: List<QuestionItem>) : ViewEvent

        data class SetResult(val result: String) : ViewEvent

        data class SetResultButtonVisibility(val isVisible: Boolean) : ViewEvent
    }

    private val compositeDisposable = CompositeDisposable()

    private var defaultLanguage: String? = null

    companion object {
        private const val MIN_WORD_AMOUNT = 4
        private const val MAX_WORD_AMOUNT_PER_QUIZ = 20
        private const val WRONG_ANSWERS_AMOUNT_PER_QUIZ = 3
    }

    fun init() {
        cacheUtils.defaultLanguage?.let {
            defaultLanguage = it
            getWords(it)
            LanguageUtils.getLanguageByCode(it)?.let {
                setEvent(BaseEvent.SetDefaultLanguage(it))
            }
        }
    }

    private fun getWords(defaultLanguage: String) {
        setEvent(BaseEvent.ShowProgress(true))
        compositeDisposable.add(
            wordRepository.getWordsByLanguage(defaultLanguage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { setEvent(BaseEvent.ShowProgress(false)) }
                .subscribeWith(object : DisposableSingleObserver<List<Word>>() {
                    override fun onSuccess(words: List<Word>) {
                        if (words.size >= MIN_WORD_AMOUNT) {
                            setEvent(BaseEvent.ShowPlaceholder(false))
                            setEvent(QuizEvent.SetResultButtonVisibility(true))
                            generateQuestions(words)
                        } else {
                            setEvent(BaseEvent.ShowPlaceholder(true))
                            setEvent(QuizEvent.SetResultButtonVisibility(false))
                        }
                    }

                    override fun onError(e: Throwable) {
                        setEvent(BaseEvent.ShowPlaceholder(true))
                        setEvent(QuizEvent.SetResultButtonVisibility(false))
                        setEvent(BaseEvent.ShowMessage(R.string.general_error))
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

        setEvent(QuizEvent.SetQuestions(questionItems))
    }

    fun getResult(questions: List<QuestionItem>?) {
        if (questions.isNullOrEmpty()) {
            setEvent(BaseEvent.ShowMessage(R.string.general_error))
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
        setEvent(BaseEvent.ShowProgress(true))
        compositeDisposable.add(
            statisticsRepository.insertStatistics(result)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    setEvent(BaseEvent.ShowProgress(false))
                    setEvent(QuizEvent.SetResult(result.result))
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

    fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

}