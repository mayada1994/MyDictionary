package com.mayada1994.mydictionary_mvvm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mayada1994.mydictionary_mvvm.R
import com.mayada1994.mydictionary_mvvm.di.DictionaryComponent
import com.mayada1994.mydictionary_mvvm.entities.LanguageInfo
import com.mayada1994.mydictionary_mvvm.entities.Statistics
import com.mayada1994.mydictionary_mvvm.entities.Word
import com.mayada1994.mydictionary_mvvm.items.QuestionItem
import com.mayada1994.mydictionary_mvvm.repositories.StatisticsRepository
import com.mayada1994.mydictionary_mvvm.repositories.WordRepository
import com.mayada1994.mydictionary_mvvm.utils.LanguageUtils
import com.mayada1994.mydictionary_mvvm.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class QuizViewModel(
    private val wordRepository: WordRepository,
    private val statisticsRepository: StatisticsRepository
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _questionsList = SingleLiveEvent<List<QuestionItem>>()
    val questionsList: LiveData<List<QuestionItem>>
        get() = _questionsList

    private val _defaultLanguage = SingleLiveEvent<LanguageInfo>()
    val defaultLanguage: LiveData<LanguageInfo>
        get() = _defaultLanguage

    private val _result = SingleLiveEvent<String>()
    val result: LiveData<String>
        get() = _result

    private val _isProgressVisible = SingleLiveEvent<Boolean>()
    val isProgressVisible: LiveData<Boolean>
        get() = _isProgressVisible

    private val _isPlaceholderVisible = SingleLiveEvent<Boolean>()
    val isPlaceholderVisible: LiveData<Boolean>
        get() = _isPlaceholderVisible

    private val _toastMessageStringResId = SingleLiveEvent<Int>()
    val toastMessageStringResId: LiveData<Int>
        get() = _toastMessageStringResId

    companion object {
        private const val MIN_WORD_AMOUNT = 4
        private const val MAX_WORD_AMOUNT_PER_QUIZ = 20
        private const val WRONG_ANSWERS_AMOUNT_PER_QUIZ = 3
    }

    fun init() {
        DictionaryComponent.cacheUtils.defaultLanguage?.let {
            getWords(it)
            LanguageUtils.getLanguageByCode(it)?.let {
                _defaultLanguage.postValue(it)
            }
        }
    }

    private fun getWords(defaultLanguage: String) {
        _isProgressVisible.postValue(true)
        compositeDisposable.add(
            wordRepository.getWordsByLanguage(defaultLanguage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { _isProgressVisible.postValue(false) }
                .subscribeWith(object : DisposableSingleObserver<List<Word>>() {
                    override fun onSuccess(words: List<Word>) {
                        if (words.size >= MIN_WORD_AMOUNT) {
                            _isPlaceholderVisible.postValue(false)
                            generateQuestions(words)
                        } else {
                            _isPlaceholderVisible.postValue(true)
                        }
                    }

                    override fun onError(e: Throwable) {
                        _isPlaceholderVisible.postValue(true)
                        _toastMessageStringResId.postValue(R.string.general_error)
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

        _questionsList.postValue(questionItems)
    }

    fun getResult(questions: List<QuestionItem>?) {
        if (questions.isNullOrEmpty()) {
            _toastMessageStringResId.postValue(R.string.general_error)
            return
        }

        defaultLanguage.value?.let { language ->
            addResultToStats(
                Statistics(
                    result = "${questions.filter { it.word.translation == it.selectedAnswer }.size}/${questions.size}",
                    timestamp = System.currentTimeMillis(),
                    language = language.locale
                )
            )
        }
    }

    private fun addResultToStats(result: Statistics) {
        _isProgressVisible.postValue(true)
        compositeDisposable.add(
            statisticsRepository.insertStatistics(result)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    _isProgressVisible.postValue(false)
                    _result.postValue(result.result)
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