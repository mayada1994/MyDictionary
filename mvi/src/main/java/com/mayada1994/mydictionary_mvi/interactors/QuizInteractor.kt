package com.mayada1994.mydictionary_mvi.interactors

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.di.DictionaryComponent
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.entities.Statistics
import com.mayada1994.mydictionary_mvi.entities.Word
import com.mayada1994.mydictionary_mvi.items.QuestionItem
import com.mayada1994.mydictionary_mvi.repositories.StatisticsRepository
import com.mayada1994.mydictionary_mvi.repositories.WordRepository
import com.mayada1994.mydictionary_mvi.states.QuizState
import com.mayada1994.mydictionary_mvi.utils.LanguageUtils
import io.reactivex.Observable
import timber.log.Timber

class QuizInteractor(
    private val wordRepository: WordRepository,
    private val statisticsRepository: StatisticsRepository
) {

    private var defaultLanguage: String? = null

    companion object {
        private const val MIN_WORD_AMOUNT = 4
        private const val MAX_WORD_AMOUNT_PER_QUIZ = 20
        private const val WRONG_ANSWERS_AMOUNT_PER_QUIZ = 3
    }

    fun getData(): Observable<QuizState> {
        DictionaryComponent.cacheUtils.defaultLanguage?.let {
            defaultLanguage = it
            LanguageUtils.getLanguageByCode(it)?.let {
                return getWords(it)
            }
        }
        return Observable.just(QuizState.ErrorState(R.string.general_error))
    }

    private fun getWords(defaultLanguage: LanguageInfo): Observable<QuizState> {
        return wordRepository.getWordsByLanguage(defaultLanguage.locale)
            .map { words ->
                if (words.size >= MIN_WORD_AMOUNT) {
                    QuizState.DataState(defaultLanguage, generateQuestions(words))
                } else {
                    QuizState.EmptyState(defaultLanguage)
                }
            }
            .onErrorReturn { QuizState.ErrorState(R.string.general_error) }
            .toObservable()
    }

    private fun generateQuestions(words: List<Word>): List<QuestionItem> {
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

        return questionItems
    }

    fun getResult(questions: List<QuestionItem>?): Observable<QuizState> {
        if (questions.isNullOrEmpty()) {
            return Observable.just(QuizState.ErrorState(R.string.general_error))
        }

        defaultLanguage?.let { language ->
            return addResultToStats(
                Statistics(
                    result = "${questions.filter { it.word.translation == it.selectedAnswer }.size}/${questions.size}",
                    timestamp = System.currentTimeMillis(),
                    language = language
                )
            )
        }

        return Observable.just(QuizState.ErrorState(R.string.general_error))
    }

    private fun addResultToStats(result: Statistics): Observable<QuizState> {
        return statisticsRepository.insertStatistics(result)
            .map<QuizState> { QuizState.ResultState(result.result) }
            .onErrorReturn { QuizState.ResultState(result.result) }
            .doOnSuccess { Timber.d("Stat added successfully") }
            .doOnError { Timber.e(it) }
            .toObservable()
    }

}