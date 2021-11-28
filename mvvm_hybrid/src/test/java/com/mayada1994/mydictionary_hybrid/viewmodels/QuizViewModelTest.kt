package com.mayada1994.mydictionary_hybrid.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.mydictionary_hybrid.R
import com.mayada1994.mydictionary_hybrid.entities.LanguageInfo
import com.mayada1994.mydictionary_hybrid.entities.Statistics
import com.mayada1994.mydictionary_hybrid.entities.Word
import com.mayada1994.mydictionary_hybrid.events.BaseEvent
import com.mayada1994.mydictionary_hybrid.events.QuizEvent
import com.mayada1994.mydictionary_hybrid.events.ViewEvent
import com.mayada1994.mydictionary_hybrid.items.QuestionItem
import com.mayada1994.mydictionary_hybrid.repositories.StatisticsRepository
import com.mayada1994.mydictionary_hybrid.repositories.WordRepository
import com.mayada1994.mydictionary_hybrid.utils.CacheUtils
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

    private val observerViewEvent: Observer<ViewEvent> = mockk()

    private val wordRepository: WordRepository = mockk()
    
    private val statisticsRepository: StatisticsRepository = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var viewModel: QuizViewModel

    @Before
    fun setup() {
        viewModel = QuizViewModel(wordRepository, statisticsRepository, cacheUtils)
        viewModel.event.observeForever(observerViewEvent)
        every { observerViewEvent.onChanged(any()) } just Runs
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
     * - call getWords with default language
     * - call getLanguageByCode in LanguageUtils which returns default languageInfo
     * - call setEvent with SetDefaultLanguage with default languageInfo
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
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            wordRepository.getWordsByLanguage(languageInfo.locale)
            observerViewEvent.onChanged(BaseEvent.ShowPlaceholder(true))
            observerViewEvent.onChanged(QuizEvent.SetResultButtonVisibility(false))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
            observerViewEvent.onChanged(BaseEvent.SetDefaultLanguage(languageInfo))
        }
    }

    /**
     * Given:
     * - getWordsByLanguage in wordRepository returns list of words of size greater or equal to MIN_WORD_AMOUNT
     * When:
     * - getWords is called with default language
     * Then should:
     * - call getWordsByLanguage in wordRepository with default language
     * - call setEvent with ShowPlaceholder with false as isVisible
     * - call setEvent with SetResultButtonVisibility with true as isVisible
     * - call generateQuestions with list of words
     * - call setEvent with SetQuestions with list of generated question items
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

        every { observerViewEvent.onChanged(any()) } just Runs

        val eventSlot = slot<ViewEvent>()

        val events = arrayListOf<Class<out ViewEvent>>()

        every { observerViewEvent.onChanged(capture(eventSlot)) } answers { events.add(eventSlot.captured::class.java) }

        //When
        viewModel.init()

        //Then
        verify {
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            wordRepository.getWordsByLanguage(languageInfo.locale)
            observerViewEvent.onChanged(BaseEvent.ShowPlaceholder(false))
            observerViewEvent.onChanged(QuizEvent.SetResultButtonVisibility(true))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }

        assert(events.contains(QuizEvent.SetQuestions::class.java))
    }

    /**
     * Given:
     * - getWordsByLanguage in wordRepository returns empty list of words (which size is less than MIN_WORD_AMOUNT)
     * When:
     * - getWords is called with default language
     * Then should:
     * - call getWordsByLanguage in wordRepository with default language
     * - call setEvent with ShowPlaceholder with true as isVisible
     * - call setEvent with SetResultButtonVisibility with false as isVisible
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
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            wordRepository.getWordsByLanguage(languageInfo.locale)
            observerViewEvent.onChanged(BaseEvent.ShowPlaceholder(true))
            observerViewEvent.onChanged(QuizEvent.SetResultButtonVisibility(false))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }
    }

    /**
     * Given:
     * - getWordsByLanguage in wordRepository throws exception
     * When:
     * - getWords is called with default language
     * Then should:
     * - call getWordsByLanguage in wordRepository with default language
     * - call setEvent with ShowPlaceholder with true as isVisible
     * - call setEvent with SetResultButtonVisibility with false as isVisible
     * - call setEvent with ShowMessage with R.string.general_error as resId
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
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            wordRepository.getWordsByLanguage(languageInfo.locale)
            observerViewEvent.onChanged(BaseEvent.ShowPlaceholder(true))
            observerViewEvent.onChanged(QuizEvent.SetResultButtonVisibility(false))
            observerViewEvent.onChanged(BaseEvent.ShowMessage(R.string.general_error))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }
    }

    @Test
    fun `When getResult is called with empty list of question items, then should call setEvent with ShowMessage with general error message`() {
        //Given
        val questionItems = emptyList<QuestionItem>()

        //When
        viewModel.getResult(questionItems)

        //Then
        verify { observerViewEvent.onChanged(BaseEvent.ShowMessage(R.string.general_error)) }
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
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            statisticsRepository.insertStatistics(capture(statSlot))
            observerViewEvent.onChanged(QuizEvent.SetResult("2/4"))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }

        assertEquals("2/4", statSlot.captured.result)
        assertEquals(language, statSlot.captured.language)
    }

}