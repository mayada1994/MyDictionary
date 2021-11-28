package com.mayada1994.mydictionary_hybrid.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.mydictionary_hybrid.R
import com.mayada1994.mydictionary_hybrid.entities.Language
import com.mayada1994.mydictionary_hybrid.entities.LanguageInfo
import com.mayada1994.mydictionary_hybrid.events.AddLanguagesEvent
import com.mayada1994.mydictionary_hybrid.events.BaseEvent
import com.mayada1994.mydictionary_hybrid.events.ViewEvent
import com.mayada1994.mydictionary_hybrid.fragments.MainFragment
import com.mayada1994.mydictionary_hybrid.repositories.LanguageRepository
import com.mayada1994.mydictionary_hybrid.utils.CacheUtils
import com.mayada1994.mydictionary_hybrid.utils.LanguageUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import io.reactivex.Completable
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class AddLanguagesViewModelTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val observerViewEvent: Observer<ViewEvent> = mockk()

    private val languageRepository: LanguageRepository = mockk()
    
    private val cacheUtils: CacheUtils = mockk()

    private lateinit var viewModel: AddLanguagesViewModel

    @Before
    fun setup() {
        viewModel = AddLanguagesViewModel(languageRepository, cacheUtils)
        viewModel.event.observeForever(observerViewEvent)
        every { observerViewEvent.onChanged(any()) } just Runs
    }

    @After
    fun clear() {
        viewModel.onDestroy()
        unmockkAll()
    }

    /**
     * When:
     * - init is called with empty list of languages
     * Then should:
     * - call setEvent with SetLanguages with list of all languages from LanguageUtils
     */
    @Test
    fun check_init() {
        //Given
        val usedLanguages = emptyList<Language>()
        val languageItems = LanguageUtils.getLanguages().map { it.toLanguageItem() }

        //When
        viewModel.init(usedLanguages)

        //Then
        verify {
            observerViewEvent.onChanged(AddLanguagesEvent.SetLanguages(languageItems))
        }

        assertEquals(true, viewModel::class.java.getDeclaredField("initialScreen").apply { isAccessible = true }.get(viewModel) as Boolean)
    }

    /**
     * When:
     * - init is called with list of languages
     * Then should:
     * - call setEvent with SetLanguages with list of all languages from LanguageUtils except given used languages
     */
    @Test
    fun check_init_partial() {
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

        //When
        viewModel.init(usedLanguages)

        //Then
        verify {
            observerViewEvent.onChanged(AddLanguagesEvent.SetLanguages(languageItems))
        }

        assertEquals(false, viewModel::class.java.getDeclaredField("initialScreen").apply { isAccessible = true }.get(viewModel) as Boolean)
    }

    @Test
    fun `When onLanguagesSelected is called with list of LanguageInfo objects, then should clear selectedLanguages and add given list to them`() {
        //Given
        val languages = listOf(
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

        //When
        viewModel.onLanguagesSelected(languages)

        //Then
        assertEquals(languages, viewModel::class.java.getDeclaredField("selectedLanguages").apply { isAccessible = true }.get(viewModel) as ArrayList<LanguageInfo>)
    }

    /**
     * Given:
     * - selectedLanguages is empty
     * When:
     * - onSaveButtonClick is called
     * Then should:
     * - call setEvent with ShowMessage with R.string.pick_languages_warning as resId
     */
    @Test
    fun check_onSaveButtonClick_empty() {
        //When
        viewModel.onSaveButtonClick()

        //Then
        verify { observerViewEvent.onChanged(BaseEvent.ShowMessage(R.string.pick_languages_warning)) }
    }

    /**
     * Given:
     * - list of selected languages
     * When:
     * - onSaveButtonClick is called
     * Then should:
     * - call saveLanguages with list of selected languages
     * - call setEvent with OnBackPressed
     */
    @Test
    fun check_onSaveButtonClick() {
        //Given
        val languages = listOf(
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

        viewModel.onLanguagesSelected(languages)

        every { languageRepository.insertLanguages(any()) } returns Completable.complete()

        viewModel.init(languages.map { it.toLanguage() })

        //When
        viewModel.onSaveButtonClick()

        //Then
        verifyOrder {
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            observerViewEvent.onChanged(AddLanguagesEvent.OnBackPressed)
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }
    }

    /**
     * Given:
     * - list of LanguageInfo objects
     * - initialScreen is true
     * When:
     * - saveLanguages is called
     * Then should:
     * - call insertLanguages in languageRepository with list of selected languages
     * - cache new default language
     * - call setEvent with ShowSelectedScreen with MainFragment::class.java
     */
    @Test
    fun check_saveLanguages_initialScreen_true() {
        //Given
        val languages = listOf(
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

        every { cacheUtils.defaultLanguage = any() } just Runs

        every { languageRepository.insertLanguages(any()) } returns Completable.complete()

        viewModel.onLanguagesSelected(languages)

        //When
        viewModel.onSaveButtonClick()

        //Then
        verifyOrder {
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            observerViewEvent.onChanged(AddLanguagesEvent.ShowSelectedScreen(MainFragment::class.java))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }
    }

    /**
     * Given:
     * - list of LanguageInfo objects
     * - initialScreen is false
     * When:
     * - saveLanguages is called
     * Then should:
     * - call insertLanguages in languageRepository with list of selected languages
     * - call setEvent with OnBackPressed
     */
    @Test
    fun check_saveLanguages_initialScreen_false() {
        //Given
        val languages = listOf(
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

        every { languageRepository.insertLanguages(any()) } returns Completable.complete()

        viewModel.init(languages.map { it.toLanguage() })

        viewModel.onLanguagesSelected(languages)

        //When
        viewModel.onSaveButtonClick()

        //Then
        verifyOrder {
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            observerViewEvent.onChanged(AddLanguagesEvent.OnBackPressed)
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }
    }

    /**
     * Given:
     * - list of LanguageInfo objects
     * - insertLanguages in languageRepository throws error
     * When:
     * - saveLanguages is called
     * Then should:
     * - call insertLanguages in languageRepository with list of selected languages
     * - call setEvent with ShowMessage with R.string.general_error as resId
     */
    @Test
    fun check_saveLanguages_error() {
        //Given
        val languages = listOf(
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

        val testException = Exception("test exception")

        every { languageRepository.insertLanguages(any()) } returns Completable.error(testException)

        viewModel.onLanguagesSelected(languages)

        //When
        viewModel.onSaveButtonClick()

        //Then
        verifyOrder {
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            observerViewEvent.onChanged(BaseEvent.ShowMessage(R.string.general_error))
        }
    }
    
}