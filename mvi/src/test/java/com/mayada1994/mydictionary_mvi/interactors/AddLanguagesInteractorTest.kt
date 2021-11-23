package com.mayada1994.mydictionary_mvi.interactors

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.entities.Language
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.fragments.MainFragment
import com.mayada1994.mydictionary_mvi.repositories.LanguageRepository
import com.mayada1994.mydictionary_mvi.states.AddLanguagesState
import com.mayada1994.mydictionary_mvi.utils.CacheUtils
import com.mayada1994.mydictionary_mvi.utils.LanguageUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddLanguagesInteractorTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val languageRepository: LanguageRepository = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var interactor: AddLanguagesInteractor

    @Before
    fun setup() {
        interactor = AddLanguagesInteractor(languageRepository, cacheUtils)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    /**
     * Given:
     * - getLanguages in LanguageUtils returns list of available languages
     * When:
     * - getLanguages is called with list of usedLanguages
     * Then should:
     * - call getLanguages in LanguageUtils
     * - return AddLanguagesState.DataState with generated list of language items
     */
    @Test
    fun check_getLanguages() {
        //Given
        val usedLanguages = emptyList<Language>()
        val languageItems = LanguageUtils.getLanguages().map { it.toLanguageItem() }
        val state = AddLanguagesState.DataState(languageItems)

        //When
        val result = interactor.getLanguages(usedLanguages)

        //Then
        result.test().assertResult(state)
    }

    /**
     * Given:
     * - getLanguages in LanguageUtils returns list of available languages
     * When:
     * - getLanguages is called with list of usedLanguages
     * Then should:
     * - call getLanguages in LanguageUtils
     * - return AddLanguagesState.DataState with generated list of language items except given used languages
     */
    @Test
    fun check_getLanguages_partial() {
        //Given
        val usedLanguages = listOf(Language("en"), Language(("fr")), Language("ar"), Language("de"), Language("es"), Language(("ko")))
        val languageItems = listOf(
            LanguageInfo(
                nameRes = R.string.chinese_language,
                locale = "zh",
                imageRes = R.drawable.ic_china
            ),
            LanguageInfo(
                nameRes = R.string.italian_language,
                locale = "it",
                imageRes = R.drawable.ic_italy
            ),
            LanguageInfo(
                nameRes = R.string.japanese_language,
                locale = "ja",
                imageRes = R.drawable.ic_japan
            ),
            LanguageInfo(
                nameRes = R.string.polish_language,
                locale = "pl",
                imageRes = R.drawable.ic_poland
            ),
            LanguageInfo(
                nameRes = R.string.turkish_language,
                locale = "tr",
                imageRes = R.drawable.ic_turkey
            )
        ).map { it.toLanguageItem() }
        val state = AddLanguagesState.DataState(languageItems)

        //When
        val result = interactor.getLanguages(usedLanguages)

        //Then
        result.test().assertResult(state)
    }

    /**
     * Given:
     * - selectedLanguages is empty
     * When:
     * - onSaveButtonClick is called with selectedLanguages and false as initialScreen
     * Then should:
     * - return AddLanguagesState.CompletedState with R.string.pick_languages_warning as resId
     */
    @Test
    fun check_onSaveButtonClick_empty() {
        //Given
        val selectedLanguages = arrayListOf<LanguageInfo>()
        val initialScreen = false
        val state = AddLanguagesState.CompletedState(R.string.pick_languages_warning)

        //When
        val result = interactor.onSaveButtonClick(selectedLanguages, initialScreen)

        //Then
        result.test().assertResult(state)
    }

    /**
     * Given:
     * - list of selected languages
     * When:
     * - onSaveButtonClick is called with selectedLanguages and false as initialScreen
     * Then should:
     * - call saveLanguages with selectedLanguages and false as initialScreen
     * - return AddLanguagesState.BackPressedState
     */
    @Test
    fun check_onSaveButtonClick() {
        //Given
        val selectedLanguages = arrayListOf(
            LanguageInfo(
                nameRes = R.string.chinese_language,
                locale = "zh",
                imageRes = R.drawable.ic_china
            ),
            LanguageInfo(
                nameRes = R.string.italian_language,
                locale = "it",
                imageRes = R.drawable.ic_italy
            ),
            LanguageInfo(
                nameRes = R.string.japanese_language,
                locale = "ja",
                imageRes = R.drawable.ic_japan
            )
        )
        val initialScreen = false
        val state = AddLanguagesState.BackPressedState

        every { languageRepository.insertLanguages(any()) } returns Single.just(Unit)

        //When
        val result = interactor.onSaveButtonClick(selectedLanguages, initialScreen)

        //Then
        result.test().assertResult(state)
    }

    /**
     * Given:
     * - list of LanguageInfo objects
     * - initialScreen is true
     * When:
     * - saveLanguages is called
     * Then should:
     * - call insertLanguages in languageDataSource with list of selected languages
     * - cache new default language
     * - return AddLanguagesState.ScreenState with MainFragment::class.java
     */
    @Test
    fun check_saveLanguages_initialScreen_true() {
        //Given
        val selectedLanguages = arrayListOf(
            LanguageInfo(
                nameRes = R.string.chinese_language,
                locale = "zh",
                imageRes = R.drawable.ic_china
            ),
            LanguageInfo(
                nameRes = R.string.italian_language,
                locale = "it",
                imageRes = R.drawable.ic_italy
            ),
            LanguageInfo(
                nameRes = R.string.japanese_language,
                locale = "ja",
                imageRes = R.drawable.ic_japan
            )
        )
        val initialScreen = true
        val state = AddLanguagesState.ScreenState(MainFragment::class.java)

        every { languageRepository.insertLanguages(any()) } returns Single.just(Unit)

        every { cacheUtils.defaultLanguage = any() } just Runs

        //When
        val result = interactor.onSaveButtonClick(selectedLanguages, initialScreen)

        //Then
        verify { languageRepository.insertLanguages(selectedLanguages.map { it.toLanguage() }) }
        result.test().assertResult(state)
    }

    /**
     * Given:
     * - list of LanguageInfo objects
     * - initialScreen is false
     * When:
     * - saveLanguages is called
     * Then should:
     * - call insertLanguages in languageDataSource with list of selected languages
     * - return AddLanguagesState.BackPressedState
     */
    @Test
    fun check_saveLanguages_initialScreen_false() {
        //Given
        val selectedLanguages = arrayListOf(
            LanguageInfo(
                nameRes = R.string.chinese_language,
                locale = "zh",
                imageRes = R.drawable.ic_china
            ),
            LanguageInfo(
                nameRes = R.string.italian_language,
                locale = "it",
                imageRes = R.drawable.ic_italy
            ),
            LanguageInfo(
                nameRes = R.string.japanese_language,
                locale = "ja",
                imageRes = R.drawable.ic_japan
            )
        )
        val initialScreen = false
        val state = AddLanguagesState.BackPressedState

        every { languageRepository.insertLanguages(any()) } returns Single.just(Unit)

        //When
        val result = interactor.onSaveButtonClick(selectedLanguages, initialScreen)

        //Then
        verify { languageRepository.insertLanguages(selectedLanguages.map { it.toLanguage() }) }
        result.test().assertResult(state)
    }

    /**
     * Given:
     * - list of LanguageInfo objects
     * - insertLanguages in languageRepository throws error
     * When:
     * - saveLanguages is called
     * Then should:
     * - call insertLanguages in languageDataSource with list of selected languages
     * - return AddLanguagesState.CompletedState with R.string.general_error as resId
     */
    @Test
    fun check_saveLanguages_initialScreen_error() {
        //Given
        val selectedLanguages = arrayListOf(
            LanguageInfo(
                nameRes = R.string.chinese_language,
                locale = "zh",
                imageRes = R.drawable.ic_china
            ),
            LanguageInfo(
                nameRes = R.string.italian_language,
                locale = "it",
                imageRes = R.drawable.ic_italy
            ),
            LanguageInfo(
                nameRes = R.string.japanese_language,
                locale = "ja",
                imageRes = R.drawable.ic_japan
            )
        )
        val initialScreen = false
        val state = AddLanguagesState.CompletedState(R.string.general_error)

        val testException = Exception("test exception")
        every { languageRepository.insertLanguages(any()) } returns Single.error(testException)

        //When
        val result = interactor.onSaveButtonClick(selectedLanguages, initialScreen)

        //Then
        verify { languageRepository.insertLanguages(selectedLanguages.map { it.toLanguage() }) }
        result.test().assertResult(state)
    }

}