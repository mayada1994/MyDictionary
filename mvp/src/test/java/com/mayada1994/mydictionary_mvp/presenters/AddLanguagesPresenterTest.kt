package com.mayada1994.mydictionary_mvp.presenters

import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.contracts.AddLanguagesContract
import com.mayada1994.mydictionary_mvp.entities.Language
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.fragments.MainFragment
import com.mayada1994.mydictionary_mvp.models.LanguageDataSource
import com.mayada1994.mydictionary_mvp.utils.CacheUtils
import com.mayada1994.mydictionary_mvp.utils.LanguageUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import io.reactivex.Completable
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddLanguagesPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val viewInterface: AddLanguagesContract.ViewInterface = mockk()

    private val languageDataSource: LanguageDataSource = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var presenter: AddLanguagesPresenter

    @Before
    fun setup() {
        presenter = AddLanguagesPresenter(viewInterface, languageDataSource, cacheUtils)
    }

    @After
    fun clear() {
        unmockkAll()
        presenter.onDestroy()
    }

    /**
     * When:
     * - init is called with empty list of languages
     * Then should:
     * - call setLanguages in viewInterface with list of all languages from LanguageUtils
     */
    @Test
    fun check_init() {
        //Given
        val usedLanguages = emptyList<Language>()
        val languageItems = LanguageUtils.getLanguages().map { it.toLanguageItem() }
        every { viewInterface.setLanguages(any()) } just Runs

        //When
        presenter.init(usedLanguages)

        //Then
        verify {
            viewInterface.setLanguages(languageItems)
        }

        assertEquals(true, presenter::class.java.getDeclaredField("initialScreen").apply { isAccessible = true }.get(presenter) as Boolean)
    }

    /**
     * When:
     * - init is called with list of languages
     * Then should:
     * - call setLanguages in viewInterface with list of all languages from LanguageUtils except given used languages
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
        every { viewInterface.setLanguages(any()) } just Runs

        //When
        presenter.init(usedLanguages)

        //Then
        verify {
            viewInterface.setLanguages(languageItems)
        }

        assertEquals(false, presenter::class.java.getDeclaredField("initialScreen").apply { isAccessible = true }.get(presenter) as Boolean)
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
        presenter.onLanguagesSelected(languages)

        //Then
        assertEquals(languages, presenter::class.java.getDeclaredField("selectedLanguages").apply { isAccessible = true }.get(presenter) as ArrayList<LanguageInfo>)
    }

    /**
     * Given:
     * - selectedLanguages is empty
     * When:
     * - onSaveButtonClick is called
     * Then should:
     * - call showMessage in viewInterface with R.string.pick_languages_warning as resId
     */
    @Test
    fun check_onSaveButtonClick_empty() {
        //Given
        every { viewInterface.showMessage(any()) } just Runs

        //When
        presenter.onSaveButtonClick()

        //Then
        verify { viewInterface.showMessage(R.string.pick_languages_warning) }
    }

    /**
     * Given:
     * - list of selected languages
     * When:
     * - onSaveButtonClick is called
     * Then should:
     * - call saveLanguages with list of selected languages
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

        presenter.onLanguagesSelected(languages)

        every { viewInterface.showProgress(any()) } just Runs

        every { languageDataSource.insertLanguages(any()) } returns Completable.complete()

        every { viewInterface.setLanguages(any()) } just Runs

        every { viewInterface.onBackPressed() } just Runs

        presenter.init(languages.map { it.toLanguage() })

        //When
        presenter.onSaveButtonClick()

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            viewInterface.onBackPressed()
            viewInterface.showProgress(false)
        }
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
     * - call setFragment in viewInterface with MainFragment::class.java
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

        every { languageDataSource.insertLanguages(any()) } returns Completable.complete()

        every { viewInterface.showProgress(any()) } just Runs

        every { viewInterface.setFragment(any()) } just Runs

        //When
        presenter.saveLanguages(languages)

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            viewInterface.setFragment(MainFragment::class.java)
            viewInterface.showProgress(false)
        }
    }

    /**
     * Given:
     * - list of LanguageInfo objects
     * - initialScreen is false
     * When:
     * - saveLanguages is called
     * Then should:
     * - call insertLanguages in languageDataSource with list of selected languages
     * - call onBackPressed in viewInterface
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

        every { languageDataSource.insertLanguages(any()) } returns Completable.complete()

        every { viewInterface.showProgress(any()) } just Runs

        every { viewInterface.onBackPressed() } just Runs

        every { viewInterface.setLanguages(any()) } just Runs

        presenter.init(languages.map { it.toLanguage() })

        //When
        presenter.saveLanguages(languages)

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            viewInterface.onBackPressed()
            viewInterface.showProgress(false)
        }
    }

    /**
     * Given:
     * - list of LanguageInfo objects
     * - insertLanguages in languageDataSource throws error
     * When:
     * - saveLanguages is called
     * Then should:
     * - call insertLanguages in languageDataSource with list of selected languages
     * - call showMessage in viewInterface with R.string.general_error as resId
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

        every { languageDataSource.insertLanguages(any()) } returns Completable.error(testException)

        every { viewInterface.showProgress(any()) } just Runs

        every { viewInterface.showMessage(any()) } just Runs

        //When
        presenter.saveLanguages(languages)

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            viewInterface.showMessage(R.string.general_error)
        }
    }

}