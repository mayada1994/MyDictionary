package com.mayada1994.mydictionary_mvvm.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.mydictionary_mvvm.R
import com.mayada1994.mydictionary_mvvm.entities.LanguageInfo
import com.mayada1994.mydictionary_mvvm.entities.Statistics
import com.mayada1994.mydictionary_mvvm.entities.Word
import com.mayada1994.mydictionary_mvvm.items.QuestionItem
import com.mayada1994.mydictionary_mvvm.repositories.StatisticsRepository
import com.mayada1994.mydictionary_mvvm.repositories.WordRepository
import com.mayada1994.mydictionary_mvvm.utils.CacheUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class QuizViewModelTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val observerDefaultLanguage: Observer<LanguageInfo> = mockk()
    private val observerQuestionsList: Observer<List<QuestionItem>> = mockk()
    private val observerResult: Observer<String> = mockk()
    private val observerIsResultButtonVisible: Observer<Boolean> = mockk()
    private val observerIsPlaceholderVisible: Observer<Boolean> = mockk()
    private val observerIsProgressVisible: Observer<Boolean> = mockk()
    private val observerToastMessageStringResId: Observer<Int> = mockk()

    private val wordRepository: WordRepository = mockk()
    
    private val statisticsRepository: StatisticsRepository = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var viewModel: QuizViewModel

    @Before
    fun setup() {
        viewModel = QuizViewModel(wordRepository, statisticsRepository, cacheUtils)
        viewModel.defaultLanguage.observeForever(observerDefaultLanguage)
        viewModel.questionsList.observeForever(observerQuestionsList)
        viewModel.result.observeForever(observerResult)
        viewModel.isResultButtonVisible.observeForever(observerIsResultButtonVisible)
        viewModel.isPlaceholderVisible.observeForever(observerIsPlaceholderVisible)
        viewModel.isProgressVisible.observeForever(observerIsProgressVisible)
        viewModel.toastMessageStringResId.observeForever(observerToastMessageStringResId)
        every { observerDefaultLanguage.onChanged(any()) } just Runs
        every { observerResult.onChanged(any()) } just Runs
        every { observerIsResultButtonVisible.onChanged(any()) } just Runs
        every { observerQuestionsList.onChanged(any()) } just Runs
        every { observerIsPlaceholderVisible.onChanged(any()) } just Runs
        every { observerIsProgressVisible.onChanged(any()) } just Runs
        every { observerToastMessageStringResId.onChanged(any()) } just Runs
    }

    @After
    fun clear() {
        viewModel.onDestroy()
        unmockkAll()
    }

    /**
     * Given:
     * - cached default language is en
     * When:
     * - init is called
     * Then should:
     * - set private field defaultLanguage value as en
     * - call getWords with default language
     * - call getLanguageByCode in LanguageUtils which returns default languageInfo
     * - post defaultLanguage with default languageInfo
     */
    @Test
    fun check_init() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        every { wordRepository.getWordsByLanguage(languageInfo.locale) } returns Single.just(emptyList())

        //When
        viewModel.init()

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            wordRepository.getWordsByLanguage(languageInfo.locale)
            observerIsPlaceholderVisible.onChanged(true)
            observerIsResultButtonVisible.onChanged(false)
            observerIsProgressVisible.onChanged(false)
            observerDefaultLanguage.onChanged(languageInfo)
        }
    }

    /**
     * Given:
     * - getWordsByLanguage in wordRepository returns list of words of size greater or equal to MIN_WORD_AMOUNT
     * When:
     * - getWords is called with default language
     * Then should:
     * - call getWordsByLanguage in wordRepository with default language
     * - post isPlaceholderVisible with false as isVisible
     * - post isResultButtonVisible with true as isVisible
     * - call generateQuestions with list of words
     * - post questionList with list of generated question items
     */
    @Test
    fun check_getWords() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        val words = listOf(
            Word("cat", "кіт", languageInfo.locale),
            Word("dog", "пес", languageInfo.locale),
            Word("parrot", "папуга", languageInfo.locale),
            Word("turtle", "черепаха", languageInfo.locale)
        )

        every { wordRepository.getWordsByLanguage(languageInfo.locale) } returns Single.just(words)

        //When
        viewModel.init()

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            wordRepository.getWordsByLanguage(languageInfo.locale)
            observerIsPlaceholderVisible.onChanged(false)
            observerIsResultButtonVisible.onChanged(true)
            observerQuestionsList.onChanged(any())
            observerIsProgressVisible.onChanged(false)
        }
    }

    /**
     * Given:
     * - getWordsByLanguage in wordRepository returns empty list of words (which size is less than MIN_WORD_AMOUNT)
     * When:
     * - getWords is called with default language
     * Then should:
     * - call getWordsByLanguage in wordRepository with default language
     * - post isPlaceholderVisible with true as isVisible
     * - post isResultButtonVisible with false as isVisible
     */
    @Test
    fun check_getWords_emptyList() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        every { wordRepository.getWordsByLanguage(languageInfo.locale) } returns Single.just(emptyList())

        //When
        viewModel.init()

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            wordRepository.getWordsByLanguage(languageInfo.locale)
            observerIsPlaceholderVisible.onChanged(true)
            observerIsResultButtonVisible.onChanged(false)
            observerIsProgressVisible.onChanged(false)
        }
    }

    /**
     * Given:
     * - getWordsByLanguage in wordRepository throws exception
     * When:
     * - getWords is called with default language
     * Then should:
     * - call getWordsByLanguage in wordRepository with default language
     * - post isPlaceholderVisible with true as isVisible
     * - post isResultButtonVisible with false as isVisible
     * - post toastMessageStringResId with R.string.general_error as resId
     */
    @Test
    fun check_getWords_error() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        val testException = Exception("test exception")
        every { wordRepository.getWordsByLanguage(languageInfo.locale) } returns Single.error(testException)

        //When
        viewModel.init()

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            wordRepository.getWordsByLanguage(languageInfo.locale)
            observerIsPlaceholderVisible.onChanged(true)
            observerIsResultButtonVisible.onChanged(false)
            observerToastMessageStringResId.onChanged(R.string.general_error)
            observerIsProgressVisible.onChanged(false)
        }
    }

    @Test
    fun `When getResult is called with empty list of question items, then should post toastMessageStringResId with general error message`() {
        //Given
        val questionItems = emptyList<QuestionItem>()

        //When
        viewModel.getResult(questionItems)

        //Then
        verify { observerToastMessageStringResId.onChanged(R.string.general_error) }
    }

    @Test
    fun `When getResult is called with some list of question items, then should call addResultToStats with created Statistics object`() {
        //Given
        val language = "en"

        every { cacheUtils.defaultLanguage } returns language

        every { wordRepository.getWordsByLanguage(any()) } returns Single.error(Exception("test exception"))

        viewModel.init()

        val words = listOf(
            Word("cat", "кіт", language),
            Word("dog", "пес", language),
            Word("parrot", "папуга", language),
            Word("turtle", "черепаха", language)
        )

        val answers = arrayListOf("кіт", "пес", "папуга", "черепаха")

        val questionItems = listOf(
            QuestionItem(word = words[0], answers = answers, selectedAnswer = answers[1]),
            QuestionItem(word = words[1], answers = answers, selectedAnswer = answers[1]),
            QuestionItem(word = words[2], answers = answers, selectedAnswer = answers[0]),
            QuestionItem(word = words[3], answers = answers, selectedAnswer = answers[3])
        )

        every { statisticsRepository.insertStatistics(any()) } returns Completable.complete()

        val statSlot = slot<Statistics>()

        //When
        viewModel.getResult(questionItems)

        //Then
        verify {
            observerIsProgressVisible.onChanged(true)
            statisticsRepository.insertStatistics(capture(statSlot))
            observerResult.onChanged("2/4")
            observerIsProgressVisible.onChanged(false)
        }

        assertEquals("2/4", statSlot.captured.result)
        assertEquals(language, statSlot.captured.language)
    }

}