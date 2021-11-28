package com.mayada1994.mydictionary_mvi.interactors

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.entities.Statistics
import com.mayada1994.mydictionary_mvi.repositories.StatisticsRepository
import com.mayada1994.mydictionary_mvi.states.StatisticsState
import com.mayada1994.mydictionary_mvi.utils.CacheUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StatisticsInteractorTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val statisticsRepository: StatisticsRepository = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var interactor: StatisticsInteractor

    @Before
    fun setup() {
        interactor = StatisticsInteractor(statisticsRepository, cacheUtils)
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
     * - set private field defaultLanguage value as en
     * - call getLanguages in LanguageUtils
     * - call getStats
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

        every { statisticsRepository.getStatisticsByLanguage(any()) } returns Single.just(emptyList())

        //When
        interactor.getData()

        //Then
        verify { statisticsRepository.getStatisticsByLanguage(languageInfo.locale) }
        assertEquals(languageInfo.locale, interactor::class.java.getDeclaredField("defaultLanguage").apply { isAccessible = true }.get(interactor) as String?)
    }

    /**
     * Given:
     * - private field defaultLanguage value is en
     * - defaultLanguage in cacheUtils returns null
     * When:
     * - getData is called
     * Then should:
     * - call getLanguages in LanguageUtils
     * - return StatisticsState.ErrorState with languageInfo for en and R.string.general_error as resId
     */
    @Test
    fun check_getData_nullDefaultLanguage() {
        //Given
        every { cacheUtils.defaultLanguage } returns "en"
        every { statisticsRepository.getStatisticsByLanguage(any()) } returns Single.just(emptyList())
        interactor.getData()
        every { cacheUtils.defaultLanguage } returns null

        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        val state = StatisticsState.ErrorState(languageInfo, R.string.general_error)

        //When
        val result = interactor.getData()

        //Then
        result.test().assertResult(state)
    }

    /**
     * Given:
     * - getStatisticsByLanguage in statisticsRepository returns list of stats
     * When:
     * - getStats is called with some languageInfo
     * Then should:
     * - call getStatisticsByLanguage in statisticsRepository
     * - return StatisticsState.DataState with given languageInfo and returned list of stats
     */
    @Test
    fun check_getStats() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        val stats = listOf(
            Statistics(
                id = 0,
                result = "17/20",
                timestamp = System.currentTimeMillis(),
                language = languageInfo.locale
            )
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        every { statisticsRepository.getStatisticsByLanguage(any()) } returns Single.just(stats)

        val state = StatisticsState.DataState(languageInfo, stats)

        //When
        val result = interactor.getData()

        //Then
        result.test().assertResult(state)
    }

    /**
     * Given:
     * - getStatisticsByLanguage in statisticsRepository returns empty list
     * When:
     * - getStats is called with some languageInfo
     * Then should:
     * - call getStatisticsByLanguage in statisticsRepository
     * - return StatisticsState.EmptyState with given languageInfo
     */
    @Test
    fun check_getStats_empty() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        every { statisticsRepository.getStatisticsByLanguage(any()) } returns Single.just(emptyList())

        val state = StatisticsState.EmptyState(languageInfo)

        //When
        val result = interactor.getData()

        //Then
        result.test().assertResult(state)
    }

    /**
     * Given:
     * - getStatisticsByLanguage in statisticsRepository throws exception
     * When:
     * - getStats is called with some languageInfo
     * Then should:
     * - call getStatisticsByLanguage in statisticsRepository
     * - return StatisticsState.ErrorState with given languageInfo and R.string.general_error as resId
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

        val state = StatisticsState.ErrorState(languageInfo, R.string.general_error)

        //When
        val result = interactor.getData()

        //Then
        result.test().assertResult(state)
    }

}