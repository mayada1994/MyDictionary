package com.mayada1994.mydictionary_hybrid.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.mydictionary_hybrid.R
import com.mayada1994.mydictionary_hybrid.entities.Language
import com.mayada1994.mydictionary_hybrid.entities.LanguageInfo
import com.mayada1994.mydictionary_hybrid.events.BaseEvent
import com.mayada1994.mydictionary_hybrid.events.DefaultLanguageEvent
import com.mayada1994.mydictionary_hybrid.events.ViewEvent
import com.mayada1994.mydictionary_hybrid.items.DefaultLanguageItem
import com.mayada1994.mydictionary_hybrid.repositories.LanguageRepository
import com.mayada1994.mydictionary_hybrid.utils.CacheUtils
import com.mayada1994.mydictionary_hybrid.utils.LanguageUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class DefaultLanguageViewModelTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val observerViewEvent: Observer<ViewEvent> = mockk()

    private val languageRepository: LanguageRepository = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var viewModel: DefaultLanguageViewModel

    @Before
    fun setup() {
        viewModel = DefaultLanguageViewModel(languageRepository, cacheUtils)
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
     * - call getLanguages
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

        every { languageRepository.getLanguages() } returns Single.error(Exception("test exception"))

        //When
        viewModel.init()

        //Then
        verifyOrder {
            observerViewEvent.onChanged(BaseEvent.SetDefaultLanguage(languageInfo))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            languageRepository.getLanguages()
            observerViewEvent.onChanged(BaseEvent.ShowMessage(R.string.general_error))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }

    }

    /**
     * Given:
     * - getLanguages in languageRepository returns list of languages
     * When:
     * - getLanguages is called
     * Then should:
     * - call getLanguages in languageRepository
     * - call generateDefaultLanguageItems with list of languages
     * - call setEvent with SetLanguages with list of generated default language items
     */
    @Test
    fun check_getLanguages() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        val languages = listOf(Language("en"), Language("fr"), Language("de"))

        every { languageRepository.getLanguages() } returns Single.just(languages)

        val languageItems = listOf(
            DefaultLanguageItem(
                nameRes = R.string.english_language,
                locale = "en",
                imageRes = R.drawable.ic_england,
                isDefault = true
            ),
            DefaultLanguageItem(
                nameRes = R.string.french_language,
                locale = "fr",
                imageRes = R.drawable.ic_france
            ),
            DefaultLanguageItem(
                nameRes = R.string.german_language,
                locale = "de",
                imageRes = R.drawable.ic_germany
            )
        )

        //When
        viewModel.init()

        //Then
        verifyOrder {
            observerViewEvent.onChanged(BaseEvent.SetDefaultLanguage(languageInfo))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            languageRepository.getLanguages()
            observerViewEvent.onChanged(DefaultLanguageEvent.SetLanguages(languageItems))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }

        assertEquals(languages, viewModel::class.java.getDeclaredField("currentLanguages").apply { isAccessible = true }.get(viewModel) as List<Language>)
    }

    /**
     * Given:
     * - getLanguages in languageRepository returns list of languages with size smaller than all available languages from LanguageUtils
     * When:
     * - getLanguages is called
     * Then should:
     * - not call setEvent with SetAddButtonVisibility with value
     */
    @Test
    fun check_getLanguages_partialLanguagesUse() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        val languages = listOf(Language("en"), Language("fr"), Language("de"))

        every { languageRepository.getLanguages() } returns Single.just(languages)

        //When
        viewModel.init()

        //Then
        verify(exactly = 0) {
            observerViewEvent.onChanged(DefaultLanguageEvent.SetAddButtonVisibility(true))
            observerViewEvent.onChanged(DefaultLanguageEvent.SetAddButtonVisibility(false))
        }
    }

    /**
     * Given:
     * - getLanguages in languageRepository returns list of languages with size equal to all available languages from LanguageUtils
     * When:
     * - getLanguages is called
     * Then should:
     * - call setEvent with SetAddButtonVisibility with false as isVisible
     */
    @Test
    fun check_getLanguages_completeLanguagesUse() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        val languages = LanguageUtils.getLanguages().map { it.toLanguage() }

        every { languageRepository.getLanguages() } returns Single.just(languages)

        //When
        viewModel.init()

        //Then
        verify {
            observerViewEvent.onChanged(DefaultLanguageEvent.SetAddButtonVisibility(false))
        }
    }

    /**
     * Given:
     * - getLanguages in languageRepository throws exception
     * When:
     * - getLanguages is called
     * Then should:
     * - call getLanguages in languageRepository
     * - call setEvent with ShowMessage with R.string.general_error as resId
     */
    @Test
    fun check_getLanguages_error() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        val testException = Exception("test exception")

        every { languageRepository.getLanguages() } returns Single.error(testException)

        //When
        viewModel.init()

        //Then
        verifyOrder {
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            languageRepository.getLanguages()
            observerViewEvent.onChanged(BaseEvent.ShowMessage(R.string.general_error))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }
    }

    @Test
    fun `Given list of current languages, when onAddButtonClick is clicked, then should call setEvent with NavigateToAddLanguagesFragment with given list`() {
        //When
        viewModel.onAddButtonClick()

        //Then
        verify { observerViewEvent.onChanged(DefaultLanguageEvent.NavigateToAddLanguagesFragment(emptyList())) }
    }

    /**
     * When:
     * - setDefaultLanguage is called with some default language item
     * Then should:
     * - call setEvent with SetDefaultLanguage with default languageInfo
     */
    @Test
    fun check_setDefaultLanguage() {
        //Given
        val defaultLanguageItem = DefaultLanguageItem(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england,
            isDefault = true
        )

        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage = any() } just Runs

        //When
        viewModel.setDefaultLanguage(defaultLanguageItem)

        //Then
        verifyOrder {
            LanguageUtils.getLanguageByCode(defaultLanguageItem.locale)
            observerViewEvent.onChanged(BaseEvent.SetDefaultLanguage(languageInfo))
        }
    }
    
}