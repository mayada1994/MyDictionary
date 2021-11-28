package com.mayada1994.mydictionary_hybrid.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.mydictionary_hybrid.R
import com.mayada1994.mydictionary_hybrid.entities.LanguageInfo
import com.mayada1994.mydictionary_hybrid.events.BaseEvent
import com.mayada1994.mydictionary_hybrid.events.ViewEvent
import com.mayada1994.mydictionary_hybrid.utils.CacheUtils
import com.mayada1994.mydictionary_hybrid.utils.LanguageUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class ResultViewModelTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val observerViewEvent: Observer<ViewEvent> = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var viewModel: ResultViewModel

    @Before
    fun setup() {
        viewModel = ResultViewModel(cacheUtils)
        viewModel.event.observeForever(observerViewEvent)
        every { observerViewEvent.onChanged(any()) } just Runs
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

        //When
        viewModel.init()

        verifyOrder {
            LanguageUtils.getLanguageByCode(languageInfo.locale)
            observerViewEvent.onChanged(BaseEvent.SetDefaultLanguage(languageInfo))
        }
    }


}