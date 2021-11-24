package com.mayada1994.mydictionary_mvp.presenters

import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.contracts.QuizContract
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.entities.Statistics
import com.mayada1994.mydictionary_mvp.entities.Word
import com.mayada1994.mydictionary_mvp.items.QuestionItem
import com.mayada1994.mydictionary_mvp.models.StatisticsDataSource
import com.mayada1994.mydictionary_mvp.models.WordDataSource
import com.mayada1994.mydictionary_mvp.utils.CacheUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuizPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val viewInterface: QuizContract.ViewInterface = mockk()

    private val wordDataSource: WordDataSource = mockk()

    private val statisticsDataSource: StatisticsDataSource = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var presenter: QuizPresenter

    @Before
    fun setup() {
        presenter = QuizPresenter(viewInterface, wordDataSource, statisticsDataSource, cacheUtils)
    }

    @After
    fun clear() {
        unmockkAll()
        presenter.onDestroy()
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
     * - call setToolbar in viewInterface with default languageInfo
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

        every { viewInterface.showProgress(any()) } just Runs

        every { wordDataSource.getWordsByLanguage(languageInfo.locale) } returns Single.just(emptyList())

        every { viewInterface.showPlaceholder(any()) } just Runs

        every { viewInterface.changeResultButtonVisibility(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.init()

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            wordDataSource.getWordsByLanguage(languageInfo.locale)
            viewInterface.showPlaceholder(true)
            viewInterface.changeResultButtonVisibility(false)
            viewInterface.showProgress(false)
            viewInterface.setToolbar(languageInfo)
        }

        assertEquals(languageInfo.locale, presenter::class.java.getDeclaredField("defaultLanguage").apply { isAccessible = true }.get(presenter) as String?)
    }

    /**
     * Given:
     * - getWordsByLanguage in wordDataSource returns list of words of size greater or equal to MIN_WORD_AMOUNT
     * When:
     * - getWords is called with default language
     * Then should:
     * - call getWordsByLanguage in wordDataSource with default language
     * - call showPlaceholder in viewInterface with false as isVisible
     * - call changeResultButtonVisibility in viewInterface with true as isVisible
     * - call generateQuestions with list of words
     * - call setQuestions in viewInterface with list of generated question items
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

        every { viewInterface.showProgress(any()) } just Runs

        val words = listOf(
            Word("cat", "кіт", languageInfo.locale),
            Word("dog", "пес", languageInfo.locale),
            Word("parrot", "папуга", languageInfo.locale),
            Word("turtle", "черепаха", languageInfo.locale)
        )

        every { wordDataSource.getWordsByLanguage(languageInfo.locale) } returns Single.just(words)

        every { viewInterface.showPlaceholder(any()) } just Runs

        every { viewInterface.changeResultButtonVisibility(any()) } just Runs

        every { viewInterface.setQuestions(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.init()

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            wordDataSource.getWordsByLanguage(languageInfo.locale)
            viewInterface.showPlaceholder(false)
            viewInterface.changeResultButtonVisibility(true)
            viewInterface.setQuestions(any())
            viewInterface.showProgress(false)
        }
    }

    /**
     * Given:
     * - getWordsByLanguage in wordDataSource returns empty list of words (which size is less than MIN_WORD_AMOUNT)
     * When:
     * - getWords is called with default language
     * Then should:
     * - call getWordsByLanguage in wordDataSource with default language
     * - call showPlaceholder in viewInterface with true as isVisible
     * - call changeResultButtonVisibility in viewInterface with false as isVisible
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

        every { viewInterface.showProgress(any()) } just Runs

        every { wordDataSource.getWordsByLanguage(languageInfo.locale) } returns Single.just(emptyList())

        every { viewInterface.showPlaceholder(any()) } just Runs

        every { viewInterface.changeResultButtonVisibility(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.init()

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            wordDataSource.getWordsByLanguage(languageInfo.locale)
            viewInterface.showPlaceholder(true)
            viewInterface.changeResultButtonVisibility(false)
            viewInterface.showProgress(false)
        }
    }

    /**
     * Given:
     * - getWordsByLanguage in wordDataSource throws exception
     * When:
     * - getWords is called with default language
     * Then should:
     * - call getWordsByLanguage in wordDataSource with default language
     * - call showPlaceholder in viewInterface with true as isVisible
     * - call changeResultButtonVisibility in viewInterface with false as isVisible
     * - call showMessage in viewInterface with R.string.general_error as resId
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

        every { viewInterface.showProgress(any()) } just Runs

        val testException = Exception("test exception")
        every { wordDataSource.getWordsByLanguage(languageInfo.locale) } returns Single.error(testException)

        every { viewInterface.showPlaceholder(any()) } just Runs

        every { viewInterface.changeResultButtonVisibility(any()) } just Runs

        every { viewInterface.showMessage(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.init()

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            wordDataSource.getWordsByLanguage(languageInfo.locale)
            viewInterface.showPlaceholder(true)
            viewInterface.changeResultButtonVisibility(false)
            viewInterface.showMessage(R.string.general_error)
            viewInterface.showProgress(false)
        }
    }

    @Test
    fun `When getResult is called with empty list of question items, then should call showMessage in viewInterface with general error message`() {
        //Given
        val questionItems = emptyList<QuestionItem>()

        every { viewInterface.showMessage(any()) } just Runs

        //When
        presenter.getResult(questionItems)

        //Then
        verify { viewInterface.showMessage(R.string.general_error) }
    }

    @Test
    fun `When getResult is called with some list of question items, then should call addResultToStats with created Statistics object`() {
        //Given
        val language = "en"

        every { cacheUtils.defaultLanguage } returns language

        every { viewInterface.showProgress(any()) } just Runs

        every { wordDataSource.getWordsByLanguage(any()) } returns Single.error(Exception("test exception"))

        every { viewInterface.showPlaceholder(any()) } just Runs

        every { viewInterface.changeResultButtonVisibility(any()) } just Runs

        every { viewInterface.showMessage(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        presenter.init()

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

        every { viewInterface.showProgress(any()) } just Runs

        every { statisticsDataSource.insertStatistics(any()) } returns Completable.complete()

        every { viewInterface.showResultFragment(any()) } just Runs

        val statSlot = slot<Statistics>()

        //When
        presenter.getResult(questionItems)

        //Then
        verify {
            viewInterface.showProgress(true)
            statisticsDataSource.insertStatistics(capture(statSlot))
            viewInterface.showResultFragment("2/4")
            viewInterface.showProgress(false)
        }

        assertEquals("2/4", statSlot.captured.result)
        assertEquals(language, statSlot.captured.language)
    }

}