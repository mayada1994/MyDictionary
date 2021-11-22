package com.mayada1994.mydictionary_mvp.presenters

import android.app.Application
import android.content.SharedPreferences
import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.contracts.DefaultLanguageContract
import com.mayada1994.mydictionary_mvp.di.DictionaryComponent
import com.mayada1994.mydictionary_mvp.entities.Language
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.items.DefaultLanguageItem
import com.mayada1994.mydictionary_mvp.models.LanguageDataSource
import com.mayada1994.mydictionary_mvp.utils.CacheUtils
import com.mayada1994.mydictionary_mvp.utils.LanguageUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DefaultLanguagePresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val viewInterface: DefaultLanguageContract.ViewInterface = mockk()

    private val languageDataSource: LanguageDataSource = mockk()

    private lateinit var presenter: DefaultLanguagePresenter

    @Before
    fun setup() {
        presenter = DefaultLanguagePresenter(viewInterface, languageDataSource)
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
     * - call getLanguages
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

        val application: Application = mockk()
        val sharedPreferences: SharedPreferences = mockk(relaxed = true)

        every { application.packageName } returns "com.mayada1994.mydictionary_mvp"
        every { application.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.getString(CacheUtils.CACHED_DEFAULT_LANGUAGE, null) } returns languageInfo.locale

        DictionaryComponent.init(application)

        every { viewInterface.showProgress(any()) } just Runs

        every { languageDataSource.getLanguages() } returns Single.error(Exception("test exception"))

        every { viewInterface.showMessage(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.init()

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            languageDataSource.getLanguages()
            viewInterface.showMessage(any())
            viewInterface.showProgress(false)
            viewInterface.setToolbar(languageInfo)
        }

        assertEquals(languageInfo.locale, presenter::class.java.getDeclaredField("defaultLanguage").apply { isAccessible = true }.get(presenter) as String?)
    }

    /**
     * Given:
     * - getLanguages in languageDataSource returns list of languages
     * When:
     * - getLanguages is called
     * Then should:
     * - call getLanguages in languageDataSource
     * - set private field currentLanguages value as returned list of languages
     * - call generateDefaultLanguageItems with list of languages
     * - call setLanguages in viewInterface with list of generated default language items
     */
    @Test
    fun check_getLanguages() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        val application: Application = mockk()
        val sharedPreferences: SharedPreferences = mockk(relaxed = true)

        every { application.packageName } returns "com.mayada1994.mydictionary_mvp"
        every { application.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.getString(CacheUtils.CACHED_DEFAULT_LANGUAGE, null) } returns languageInfo.locale

        DictionaryComponent.init(application)

        every { viewInterface.showProgress(any()) } just Runs

        val languages = listOf(Language("en"), Language("fr"), Language("de"))

        every { languageDataSource.getLanguages() } returns Single.just(languages)

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

        every { viewInterface.setLanguages(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.init()

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            languageDataSource.getLanguages()
            viewInterface.setLanguages(languageItems)
            viewInterface.showProgress(false)
        }

        assertEquals(languages, presenter::class.java.getDeclaredField("currentLanguages").apply { isAccessible = true }.get(presenter) as List<Language>)
    }

    /**
     * Given:
     * - getLanguages in languageDataSource returns list of languages with size smaller than all available languages from LanguageUtils
     * When:
     * - getLanguages is called
     * Then should:
     * - not call changeAddButtonVisibility in viewInterface
     */
    @Test
    fun check_getLanguages_partialLanguagesUse() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        val application: Application = mockk()
        val sharedPreferences: SharedPreferences = mockk(relaxed = true)

        every { application.packageName } returns "com.mayada1994.mydictionary_mvp"
        every { application.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.getString(CacheUtils.CACHED_DEFAULT_LANGUAGE, null) } returns languageInfo.locale

        DictionaryComponent.init(application)

        every { viewInterface.showProgress(any()) } just Runs

        val languages = listOf(Language("en"), Language("fr"), Language("de"))

        every { languageDataSource.getLanguages() } returns Single.just(languages)

        every { viewInterface.setLanguages(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.init()

        //Then
        verify(exactly = 0) {
            viewInterface.changeAddButtonVisibility(any())
        }
    }

    /**
     * Given:
     * - getLanguages in languageDataSource returns list of languages with size equal to all available languages from LanguageUtils
     * When:
     * - getLanguages is called
     * Then should:
     * - call changeAddButtonVisibility in viewInterface with false as isVisible
     */
    @Test
    fun check_getLanguages_completeLanguagesUse() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        val application: Application = mockk()
        val sharedPreferences: SharedPreferences = mockk(relaxed = true)

        every { application.packageName } returns "com.mayada1994.mydictionary_mvp"
        every { application.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.getString(CacheUtils.CACHED_DEFAULT_LANGUAGE, null) } returns languageInfo.locale

        DictionaryComponent.init(application)

        every { viewInterface.showProgress(any()) } just Runs

        val languages = LanguageUtils.getLanguages().map { it.toLanguage() }

        every { languageDataSource.getLanguages() } returns Single.just(languages)

        every { viewInterface.setLanguages(any()) } just Runs

        every { viewInterface.changeAddButtonVisibility(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.init()

        //Then
        verify {
            viewInterface.changeAddButtonVisibility(false)
        }
    }

    /**
     * Given:
     * - getLanguages in languageDataSource throws exception
     * When:
     * - getLanguages is called
     * Then should:
     * - call getLanguages in languageDataSource
     * - call showMessage in viewInterface with R.string.general_error as resId
     */
    @Test
    fun check_getLanguages_error() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        val application: Application = mockk()
        val sharedPreferences: SharedPreferences = mockk(relaxed = true)

        every { application.packageName } returns "com.mayada1994.mydictionary_mvp"
        every { application.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.getString(CacheUtils.CACHED_DEFAULT_LANGUAGE, null) } returns languageInfo.locale

        DictionaryComponent.init(application)

        every { viewInterface.showProgress(any()) } just Runs

        val testException = Exception("test exception")

        every { languageDataSource.getLanguages() } returns Single.error(testException)

        every { viewInterface.showMessage(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.init()

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            languageDataSource.getLanguages()
            viewInterface.showMessage(R.string.general_error)
            viewInterface.showProgress(false)
        }
    }

    @Test
    fun `Given list of current languages, when onAddButtonClick is clicked, then call navigateToAddLanguagesFragment in viewInterface with given list`() {
        //Given
        every { viewInterface.navigateToAddLanguagesFragment(any()) } just Runs

        //When
        presenter.onAddButtonClick()

        //Then
        verifyOrder { viewInterface.navigateToAddLanguagesFragment(emptyList()) }
    }

    /**
     * When:
     * - setDefaultLanguage is called with some default language item
     * Then should:
     * - set private field defaultLanguage value as en
     * - call setToolbar in viewInterface with default languageInfo
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

        val application: Application = mockk()
        val sharedPreferences: SharedPreferences = mockk(relaxed = true)
        val editor: SharedPreferences.Editor = mockk(relaxed = true)

        every { application.packageName } returns "com.mayada1994.mydictionary_mvp"
        every { application.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor

        DictionaryComponent.init(application)

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.setDefaultLanguage(defaultLanguageItem)

        //Then
        verifyOrder {
            LanguageUtils.getLanguageByCode(defaultLanguageItem.locale)
            viewInterface.setToolbar(languageInfo)
        }
        assertEquals(defaultLanguageItem.locale, presenter::class.java.getDeclaredField("defaultLanguage").apply { isAccessible = true }.get(presenter) as String?)
    }

}