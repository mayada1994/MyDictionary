package com.mayada1994.mydictionary_mvvm.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.mydictionary_mvvm.R
import com.mayada1994.mydictionary_mvvm.entities.LanguageInfo
import com.mayada1994.mydictionary_mvvm.entities.Statistics
import com.mayada1994.mydictionary_mvvm.repositories.StatisticsRepository
import com.mayada1994.mydictionary_mvvm.utils.CacheUtils
import com.mayada1994.mydictionary_mvvm.utils.LanguageUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class StatisticsViewModelTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val observerDefaultLanguage: Observer<LanguageInfo> = mockk()
    private val observerStatsList: Observer<List<Statistics>> = mockk()
    private val observerIsPlaceholderVisible: Observer<Boolean> = mockk()
    private val observerIsProgressVisible: Observer<Boolean> = mockk()
    private val observerToastMessageStringResId: Observer<Int> = mockk()

    private val statisticsRepository: StatisticsRepository = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var viewModel: StatisticsViewModel

    @Before
    fun setup() {
        viewModel = StatisticsViewModel(statisticsRepository, cacheUtils)
        viewModel.defaultLanguage.observeForever(observerDefaultLanguage)
        viewModel.statsList.observeForever(observerStatsList)
        viewModel.isPlaceholderVisible.observeForever(observerIsPlaceholderVisible)
        viewModel.isProgressVisible.observeForever(observerIsProgressVisible)
        viewModel.toastMessageStringResId.observeForever(observerToastMessageStringResId)
        every { observerDefaultLanguage.onChanged(any()) } just Runs
        every { observerStatsList.onChanged(any()) } just Runs
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
     * - call getStats with default language
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

        every { statisticsRepository.getStatisticsByLanguage(any()) } returns Single.just(emptyList())

        //When
        viewModel.init()

        //Then
        verifyOrder {
            LanguageUtils.getLanguageByCode(languageInfo.locale)
            observerIsProgressVisible.onChanged(true)
            observerIsPlaceholderVisible.onChanged(any())
            observerIsProgressVisible.onChanged(false)
            observerDefaultLanguage.onChanged(languageInfo)
        }
    }

    /**
     * Given:
     * - getStatisticsByLanguage in statisticsRepository returns list of stats
     * When:
     * - getStats is called with default language
     * Then should:
     * - call getStatisticsByLanguage in statisticsRepository with default language
     * - post isPlaceholderVisible with false as isVisible
     * - post statsList with list of stats
     */
    @Test
    fun check_getStats() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        val stats = listOf(
            Statistics(
                id = 0,
                result = "17/20",
                timestamp = System.currentTimeMillis(),
                language = languageInfo.locale
            )
        )

        every { statisticsRepository.getStatisticsByLanguage(any()) } returns Single.just(stats)

        //When
        viewModel.init()

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            observerIsPlaceholderVisible.onChanged(false)
            observerStatsList.onChanged(stats)
            observerIsProgressVisible.onChanged(false)
        }
    }

    /**
     * Given:
     * - getStatisticsByLanguage in statisticsRepository returns empty list
     * When:
     * - getStats is called with default language
     * Then should:
     * - call getStatisticsByLanguage in statisticsRepository with default language
     * - post isPlaceholderVisible with true as isVisible
     */
    @Test
    fun check_getStats_emptyList() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        every { statisticsRepository.getStatisticsByLanguage(any()) } returns Single.just(emptyList())

        //When
        viewModel.init()

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            observerIsPlaceholderVisible.onChanged(true)
            observerIsProgressVisible.onChanged(false)
        }
    }

    /**
     * Given:
     * - getStatisticsByLanguage in statisticsRepository throws exception
     * When:
     * - getStats is called with default language
     * Then should:
     * - call getStatisticsByLanguage in statisticsRepository with default language
     * - post isPlaceholderVisible with true as isVisible
     * - post toastMessageStringResId with R.string.general_error as resId
     */
    @Test
    fun check_getStats_error() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        val testException = Exception("test exception")
        every { statisticsRepository.getStatisticsByLanguage(any()) } returns Single.error(testException)

        //When
        viewModel.init()

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            observerIsPlaceholderVisible.onChanged(true)
            observerToastMessageStringResId.onChanged(R.string.general_error)
            observerIsProgressVisible.onChanged(false)
        }
    }
    
}