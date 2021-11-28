package com.mayada1994.mydictionary_mvi.interactors

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.entities.Language
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.items.DefaultLanguageItem
import com.mayada1994.mydictionary_mvi.repositories.LanguageRepository
import com.mayada1994.mydictionary_mvi.states.DefaultLanguageState
import com.mayada1994.mydictionary_mvi.utils.CacheUtils
import com.mayada1994.mydictionary_mvi.utils.LanguageUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DefaultLanguageInteractorTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val languageRepository: LanguageRepository = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var interactor: DefaultLanguageInteractor

    @Before
    fun setup() {
        interactor = DefaultLanguageInteractor(languageRepository, cacheUtils)
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
     * - set private field defaultLanguage value as en
     * - return DefaultLanguageState.DataState
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

        every { languageRepository.getLanguages() } returns Single.just(emptyList())

        val state = DefaultLanguageState.DataState(languageInfo, emptyList(), true)

        //When
        val result = interactor.getData()

        //Then
        result.test().assertResult(state)
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
     * - return DefaultLanguageState.ErrorState with languageInfo for en and R.string.general_error as resId
     */
    @Test
    fun check_getData_nullDefaultLanguage() {
        //Given
        every { cacheUtils.defaultLanguage } returns "en"
        every { languageRepository.getLanguages() } returns Single.just(emptyList())
        interactor.getData()
        every { cacheUtils.defaultLanguage } returns null

        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        val state = DefaultLanguageState.ErrorState(languageInfo, R.string.general_error)

        //When
        val result = interactor.getData()

        //Then
        result.test().assertResult(state)
    }

    /**
     * When:
     * - getLanguages is called with some languageInfo
     * Then should:
     * - call getLanguages in languageRepository which returns list of languages
     * - set private field currentLanguages value as returned list of languages
     * - call generateDefaultLanguageItems
     * - return DefaultLanguageState.DataState with given languageInfo and list of generated default language items
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

        val state = DefaultLanguageState.DataState(languageInfo, languageItems, true)

        //When
        val result = interactor.getData()

        //Then
        result.test().assertResult(state)
        assertEquals(languages, interactor::class.java.getDeclaredField("currentLanguages").apply { isAccessible = true }.get(interactor) as List<Language>)
    }

    /**
     * When:
     * - getLanguages is called with some languageInfo
     * Then should:
     * - call getLanguages in languageRepository which returns list of all available languages
     * - set private field currentLanguages value as returned list of languages
     * - call generateDefaultLanguageItems
     * - return DefaultLanguageState.DataState with given languageInfo and list of generated default language items
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

        val languageItems = LanguageUtils.getLanguages().map { it.toDefaultLanguageItem() }.onEach {
            if (it.locale == languageInfo.locale) {
                it.isDefault = true
            }
        }

        val state = DefaultLanguageState.DataState(languageInfo, languageItems, false)

        //When
        val result = interactor.getData()

        //Then
        result.test().assertResult(state)
        assertEquals(languages, interactor::class.java.getDeclaredField("currentLanguages").apply { isAccessible = true }.get(interactor) as List<Language>)
    }

    /**
     * When:
     * - getLanguages in languageRepository throws exception
     * Then should:
     * - call getLanguages in languageRepository
     * - return DefaultLanguageState.ErrorState with given languageInfo and R.string.general_error as resId
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

        val state = DefaultLanguageState.ErrorState(
            defaultLanguage = languageInfo,
            resId = R.string.general_error
        )

        //When
        val result = interactor.getData()

        //Then
        result.test().assertResult(state)
    }

    @Test
    fun `When onAddButtonClick is clicked, then should return NavigateToAddLanguagesFragmentState with given list of current languages`() {
        //Given
        val state = DefaultLanguageState.NavigateToAddLanguagesFragmentState(emptyList())

        //When
        val result = interactor.onAddButtonClick()

        //Then
        result.test().assertResult(state)
    }

    /**
     * When:
     * - setDefaultLanguage is called with some default language item
     * Then should:
     * - set private field defaultLanguage value as en
     * - return DefaultLanguageState.ToolbarState with default languageInfo
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

        val state = DefaultLanguageState.ToolbarState(languageInfo)

        every { cacheUtils.defaultLanguage = any() } just Runs

        //When
        val result = interactor.setDefaultLanguage(defaultLanguageItem)

        //Then
        result.test().assertResult(state)
        assertEquals(defaultLanguageItem.locale, interactor::class.java.getDeclaredField("defaultLanguage").apply { isAccessible = true }.get(interactor) as String?)
    }

    /**
     * When:
     * - setDefaultLanguage is called with some unknown default language item
     * Then should:
     * - return DefaultLanguageState.ErrorState with default languageInfo and R.string.general_error as resId
     */
    @Test
    fun check_setDefaultLanguage_other() {
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

        val state = DefaultLanguageState.ErrorState(languageInfo, R.string.general_error)

        every { cacheUtils.defaultLanguage = any() } just Runs

        interactor.setDefaultLanguage(defaultLanguageItem)

        val otherDefaultLanguageItem = DefaultLanguageItem(
            nameRes = R.string.english_language,
            locale = "eg",
            imageRes = R.drawable.ic_england,
            isDefault = true
        )

        //When
        val result = interactor.setDefaultLanguage(otherDefaultLanguageItem)

        //Then
        result.test().assertResult(state)
    }

}