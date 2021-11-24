package com.mayada1994.mydictionary_hybrid.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.mydictionary_hybrid.R
import com.mayada1994.mydictionary_hybrid.entities.LanguageInfo
import com.mayada1994.mydictionary_hybrid.entities.Statistics
import com.mayada1994.mydictionary_hybrid.events.BaseEvent
import com.mayada1994.mydictionary_hybrid.events.StatisticsEvent
import com.mayada1994.mydictionary_hybrid.events.ViewEvent
import com.mayada1994.mydictionary_hybrid.repositories.StatisticsRepository
import com.mayada1994.mydictionary_hybrid.utils.CacheUtils
import com.mayada1994.mydictionary_hybrid.utils.LanguageUtils
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

    private val observerViewEvent: Observer<ViewEvent> = mockk()

    private val statisticsRepository: StatisticsRepository = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var viewModel: StatisticsViewModel

    @Before
    fun setup() {
        viewModel = StatisticsViewModel(statisticsRepository, cacheUtils)
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
     * - call getStats with default language
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

        every { statisticsRepository.getStatisticsByLanguage(any()) } returns Single.just(emptyList())

        //When
        viewModel.init()

        //Then
        verifyOrder {
            LanguageUtils.getLanguageByCode(languageInfo.locale)
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            observerViewEvent.onChanged(BaseEvent.ShowPlaceholder(true))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
            observerViewEvent.onChanged(BaseEvent.SetDefaultLanguage(languageInfo))
        }
    }

    /**
     * Given:
     * - getStatisticsByLanguage in statisticsRepository returns list of stats
     * When:
     * - getStats is called with default language
     * Then should:
     * - call getStatisticsByLanguage in statisticsRepository with default language
     * - call setEvent with ShowPlaceholder with false as isVisible
     * - call setEvent with SetStats with list of stats
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
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            observerViewEvent.onChanged(BaseEvent.ShowPlaceholder(false))
            observerViewEvent.onChanged(StatisticsEvent.SetStats(stats))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }
    }

    /**
     * Given:
     * - getStatisticsByLanguage in statisticsRepository returns empty list
     * When:
     * - getStats is called with default language
     * Then should:
     * - call getStatisticsByLanguage in statisticsRepository with default language
     * - call setEvent with ShowPlaceholder with true as isVisible
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
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            observerViewEvent.onChanged(BaseEvent.ShowPlaceholder(true))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }
    }

    /**
     * Given:
     * - getStatisticsByLanguage in statisticsRepository throws exception
     * When:
     * - getStats is called with default language
     * Then should:
     * - call getStatisticsByLanguage in statisticsRepository with default language
     * - call setEvent with ShowPlaceholder with true as isVisible
     * - call setEvent with ShowMessage with R.string.general_error as resId
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
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            observerViewEvent.onChanged(BaseEvent.ShowPlaceholder(true))
            observerViewEvent.onChanged(BaseEvent.ShowMessage(R.string.general_error))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }
    }
    
}