package com.mayada1994.mydictionary_mvi.interactors

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.entities.Statistics
import com.mayada1994.mydictionary_mvi.entities.Word
import com.mayada1994.mydictionary_mvi.items.QuestionItem
import com.mayada1994.mydictionary_mvi.repositories.StatisticsRepository
import com.mayada1994.mydictionary_mvi.repositories.WordRepository
import com.mayada1994.mydictionary_mvi.states.QuizState
import com.mayada1994.mydictionary_mvi.utils.CacheUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuizInteractorTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val wordRepository: WordRepository = mockk()

    private val statisticsRepository: StatisticsRepository = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var interactor: QuizInteractor

    @Before
    fun setup() {
        interactor = QuizInteractor(wordRepository, statisticsRepository, cacheUtils)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    /**
     * Given:
     * - defaultLanguage in cacheUtils returns en
     * When:
     * - getData is called
     * Then should:
     * - call getLanguages in LanguageUtils
     * - set private field defaultLanguage value as en
     * - call getWords with languageInfo
     * - call getWordsByLanguage in wordRepository
     */
    @Test
    fun check_getData() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        every { wordRepository.getWordsByLanguage(any()) } returns Single.just(emptyList())

        //When
        interactor.getData()

        //Then
        verify { wordRepository.getWordsByLanguage(languageInfo.locale) }
        assertEquals(languageInfo.locale, interactor::class.java.getDeclaredField("defaultLanguage").apply { isAccessible = true }.get(interactor) as String?)
    }

    /**
     * Given:
     * - defaultLanguage in cacheUtils returns null
     * When:
     * - getData is called
     * Then should:
     * - call getLanguages in LanguageUtils
     * - return QuizState.ErrorState with R.string.general_error as resId
     */
    @Test
    fun check_getData_nullDefaultLanguage() {
        //Given
        every { cacheUtils.defaultLanguage } returns null

        val state = QuizState.ErrorState(R.string.general_error)

        //When
        val result = interactor.getData()

        //Then
        result.test().assertResult(state)
    }

    /**
     * Given:
     * - getWordsByLanguage in wordRepository returns list of words of size greater or equal to MIN_WORD_AMOUNT
     * When:
     * - getWords is called with some languageInfo
     * Then should:
     * - call generateQuestions with returned words
     * - return QuizState.DataState with given languageInfo and generated list of question items
     */
    @Test
    fun check_getWords() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        val words = listOf(
            Word("cat", "кіт", languageInfo.locale),
            Word("dog", "пес", languageInfo.locale),
            Word("parrot", "папуга", languageInfo.locale),
            Word("turtle", "черепаха", languageInfo.locale)
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        every { wordRepository.getWordsByLanguage(any()) } returns Single.just(words)

        //When
        val result = interactor.getData()

        //Then
        assertEquals(QuizState.DataState::class, result.blockingFirst()::class)
    }

    /**
     * Given:
     * - getWordsByLanguage in wordRepository returns empty list of words (which size is less than MIN_WORD_AMOUNT)
     * When:
     * - getWords is called with some languageInfo
     * Then should:
     * - return QuizState.EmptyState with given languageInfo
     */
    @Test
    fun check_getWords_empty() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        every { wordRepository.getWordsByLanguage(any()) } returns Single.just(emptyList())

        val state = QuizState.EmptyState(languageInfo)

        //When
        val result = interactor.getData()

        //Then
        result.test().assertResult(state)
    }

    /**
     * Given:
     * - getWordsByLanguage in wordRepository throws exception
     * When:
     * - getWords is called with some languageInfo
     * Then should:
     * - return QuizState.ErrorState with R.string.general_error as resId
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

        every { wordRepository.getWordsByLanguage(any()) } returns Single.error(testException)

        val state = QuizState.ErrorState(R.string.general_error)

        //When
        val result = interactor.getData()

        //Then
        result.test().assertResult(state)
    }

    @Test
    fun `When getResult is called with empty list of question items, then should return ErrorState with general error message`() {
        //Given
        val questionItems = emptyList<QuestionItem>()

        val state = QuizState.ErrorState(R.string.general_error)

        //When
        val result = interactor.getResult(questionItems)

        //Then
        result.test().assertResult(state)
    }

    @Test
    fun `When getResult is called with some list of question items, then should call addResultToStats with created Statistics object`() {
        //Given
        val language = "en"

        every { cacheUtils.defaultLanguage } returns language

        every { wordRepository.getWordsByLanguage(any()) } returns Single.error(Exception("test exception"))

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

        interactor.getData()

        every { statisticsRepository.insertStatistics(any()) } returns Single.just(Unit)

        val statSlot = slot<Statistics>()

        //When
        interactor.getResult(questionItems)

        //Then
        verify { statisticsRepository.insertStatistics(capture(statSlot)) }

        assertEquals("2/4", statSlot.captured.result)
        assertEquals(language, statSlot.captured.language)
    }

    @Test
    fun `Given insertStatistics in statisticsRepository returns Single, when addResultToStats is called with some stats, then should return ResultState with some result`() {
        //Given
        val language = "en"

        every { cacheUtils.defaultLanguage } returns language

        every { wordRepository.getWordsByLanguage(any()) } returns Single.error(Exception("test exception"))

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

        interactor.getData()

        every { statisticsRepository.insertStatistics(any()) } returns Single.just(Unit)

        val state = QuizState.ResultState("2/4")

        //When
        val result = interactor.getResult(questionItems)

        //Then
        result.test().assertResult(state)
    }

    @Test
    fun `Given insertStatistics in statisticsRepository throws exception, when addResultToStats is called with some stats, then should return ResultState with some result`() {
        //Given
        val language = "en"

        every { cacheUtils.defaultLanguage } returns language

        every { wordRepository.getWordsByLanguage(any()) } returns Single.error(Exception("test exception"))

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

        interactor.getData()

        val testException = Exception("test exception")
        every { statisticsRepository.insertStatistics(any()) } returns Single.error(testException)

        val state = QuizState.ResultState("2/4")

        //When
        val result = interactor.getResult(questionItems)

        //Then
        result.test().assertResult(state)
    }

}