package com.mayada1994.mydictionary_mvi.interactors

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.states.ResultState
import com.mayada1994.mydictionary_mvi.utils.CacheUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ResultInteractorTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var interactor: ResultInteractor

    @Before
    fun setup() {
        interactor = ResultInteractor(cacheUtils)
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
     * - return ResultState.DataState with languageInfo
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

        val state = ResultState.DataState(languageInfo)

        //When
        val result = interactor.getData()

        //Then
        result.test().assertResult(state)
    }

    /**
     * Given:
     * - defaultLanguage in cacheUtils returns null
     * When:
     * - getData is called
     * Then should:
     * - return ResultState.ErrorState with R.string.general_error as resId
     */
    @Test
    fun check_getData_nullDefaultLanguage() {
        //Given
        every { cacheUtils.defaultLanguage } returns null

        val state = ResultState.ErrorState(R.string.general_error)

        //When
        val result = interactor.getData()

        //Then
        result.test().assertResult(state)
    }

}