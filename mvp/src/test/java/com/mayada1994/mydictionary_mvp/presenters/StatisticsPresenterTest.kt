package com.mayada1994.mydictionary_mvp.presenters

import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.contracts.StatisticsContract
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.entities.Statistics
import com.mayada1994.mydictionary_mvp.models.StatisticsDataSource
import com.mayada1994.mydictionary_mvp.utils.CacheUtils
import com.mayada1994.mydictionary_mvp.utils.LanguageUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StatisticsPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val viewInterface: StatisticsContract.ViewInterface = mockk()

    private val statisticsDataSource: StatisticsDataSource = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var presenter: StatisticsPresenter

    @Before
    fun setup() {
        presenter = StatisticsPresenter(viewInterface, statisticsDataSource, cacheUtils)
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
     * - call getStats with default language
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

        every { statisticsDataSource.getStatisticsByLanguage(any()) } returns Single.just(emptyList())

        every { viewInterface.showPlaceholder(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.init()

        //Then
        verifyOrder {
            LanguageUtils.getLanguageByCode(languageInfo.locale)
            viewInterface.showProgress(true)
            viewInterface.showPlaceholder(any())
            viewInterface.showProgress(false)
            viewInterface.setToolbar(languageInfo)
        }
    }

    /**
     * Given:
     * - getStatisticsByLanguage in statisticsDataSource returns list of stats
     * When:
     * - getStats is called with default language
     * Then should:
     * - call getStatisticsByLanguage in statisticsDataSource with default language
     * - call showPlaceholder in viewInterface with false as isVisible
     * - call setStats in viewInterface with list of stats
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

        every { viewInterface.showProgress(any()) } just Runs

        val stats = listOf(
            Statistics(
                id = 0,
                result = "17/20",
                timestamp = System.currentTimeMillis(),
                language = languageInfo.locale
            )
        )

        every { statisticsDataSource.getStatisticsByLanguage(any()) } returns Single.just(stats)

        every { viewInterface.showPlaceholder(any()) } just Runs

        every { viewInterface.setStats(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.init()

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            viewInterface.showPlaceholder(false)
            viewInterface.setStats(stats)
            viewInterface.showProgress(false)
        }
    }

    /**
     * Given:
     * - getStatisticsByLanguage in statisticsDataSource returns empty list
     * When:
     * - getStats is called with default language
     * Then should:
     * - call getStatisticsByLanguage in statisticsDataSource with default language
     * - call showPlaceholder in viewInterface with true as isVisible
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

        every { viewInterface.showProgress(any()) } just Runs

        every { statisticsDataSource.getStatisticsByLanguage(any()) } returns Single.just(emptyList())

        every { viewInterface.showPlaceholder(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.init()

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            viewInterface.showPlaceholder(true)
            viewInterface.showProgress(false)
        }
    }

    /**
     * Given:
     * - getStatisticsByLanguage in statisticsDataSource throws exception
     * When:
     * - getStats is called with default language
     * Then should:
     * - call getStatisticsByLanguage in statisticsDataSource with default language
     * - call showPlaceholder in viewInterface with true as isVisible
     * - call showMessage in viewInterface with R.string.general_error as resId
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

        every { viewInterface.showProgress(any()) } just Runs

        val testException = Exception("test exception")
        every { statisticsDataSource.getStatisticsByLanguage(any()) } returns Single.error(testException)

        every { viewInterface.showPlaceholder(any()) } just Runs

        every { viewInterface.showMessage(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.init()

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            viewInterface.showPlaceholder(true)
            viewInterface.showMessage(R.string.general_error)
            viewInterface.showProgress(false)
        }
    }

}