package com.mayada1994.mydictionary_mvp.presenters

import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.contracts.ResultContract
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.utils.CacheUtils
import com.mayada1994.mydictionary_mvp.utils.LanguageUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ResultPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val viewInterface: ResultContract.ViewInterface = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var presenter: ResultPresenter

    @Before
    fun setup() {
        presenter = ResultPresenter(viewInterface, cacheUtils)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    /**
     * Given:
     * - cached default language is en
     * When:
     * - init is called
     * Then should:
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

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.init()

        verifyOrder {
            LanguageUtils.getLanguageByCode(languageInfo.locale)
            viewInterface.setToolbar(languageInfo)
        }
    }

}