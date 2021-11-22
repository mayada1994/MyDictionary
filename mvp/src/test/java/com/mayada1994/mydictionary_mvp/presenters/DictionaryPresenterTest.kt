package com.mayada1994.mydictionary_mvp.presenters

import android.app.Application
import android.content.SharedPreferences
import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.contracts.DictionaryContract
import com.mayada1994.mydictionary_mvp.di.DictionaryComponent
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.entities.Word
import com.mayada1994.mydictionary_mvp.models.WordDataSource
import com.mayada1994.mydictionary_mvp.utils.CacheUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DictionaryPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val viewInterface: DictionaryContract.ViewInterface = mockk()

    private val wordDataSource: WordDataSource = mockk()

    private lateinit var presenter: DictionaryPresenter

    @Before
    fun setup() {
        presenter = DictionaryPresenter(viewInterface, wordDataSource)
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
     * - call getWords with default language
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

        every { wordDataSource.getWordsByLanguage(languageInfo.locale) } returns Single.just(emptyList())

        every { viewInterface.showPlaceholder(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.init()

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            wordDataSource.getWordsByLanguage(languageInfo.locale)
            viewInterface.showPlaceholder(true)
            viewInterface.showProgress(false)
            viewInterface.setToolbar(languageInfo)
        }

        assertEquals(languageInfo.locale, presenter::class.java.getDeclaredField("defaultLanguage").apply { isAccessible = true }.get(presenter) as String?)
    }

    @Test
    fun `When onAddButtonClick is called, then should call showAddNewWordDialog in viewInterface`() {
        //Given
        every { viewInterface.showAddNewWordDialog() } just Runs

        //When
        presenter.onAddButtonClick()

        //Then
        verify { viewInterface.showAddNewWordDialog() }
    }

    @Test
    fun `When onSaveButtonClick is called with null as word, then should call showMessage in viewInterface with fill all fields prompt`() {
        //Given
        val word: String? = null
        val translation = "кіт"

        every { viewInterface.showMessage(any()) } just Runs

        //When
        presenter.onSaveButtonClick(word, translation)

        //Then
        verify { viewInterface.showMessage(R.string.fill_all_fields_prompt) }
    }

    @Test
    fun `When onSaveButtonClick is called with null as translation, then should call showMessage in viewInterface with fill all fields prompt`() {
        //Given
        val word = "cat"
        val translation: String? = null

        every { viewInterface.showMessage(any()) } just Runs

        //When
        presenter.onSaveButtonClick(word, translation)

        //Then
        verify { viewInterface.showMessage(R.string.fill_all_fields_prompt) }
    }

    @Test
    fun `When onSaveButtonClick is called with word and translation, then should call addWordToDictionary with created Word object`() {
        //Given
        val word = "cat"
        val translation = "кіт"

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

        every { wordDataSource.getWordsByLanguage(languageInfo.locale) } returns Single.just(emptyList())

        every { viewInterface.showPlaceholder(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        every { viewInterface.showMessage(any()) } just Runs

        presenter.init()

        every { wordDataSource.insertWord(any()) } returns Completable.complete()

        //When
        presenter.onSaveButtonClick(word, translation)

        //Then
        verify { wordDataSource.insertWord(Word(word, translation, languageInfo.locale)) }
    }

    @Test
    fun `When onDeleteButtonClick is called with some word, then should call deleteWordFromDictionary with given word`() {
        //Given
        val word = Word("cat", "кіт", "en")

        every { viewInterface.showMessage(any()) } just Runs

        every { viewInterface.showProgress(any()) } just Runs

        every { wordDataSource.deleteWord(any()) } returns Completable.complete()

        //When
        presenter.onDeleteButtonClick(word)

        //Then
        verify { wordDataSource.deleteWord(word) }
    }

    /**
     * Given:
     * - getWordsByLanguage in wordDataSource returns list of words
     * When:
     * - getWords is called with default language
     * Then should:
     * - call getWordsByLanguage in wordDataSource with default language
     * - call showPlaceholder in viewInterface with false as isVisible
     * - call setWords in viewInterface with list of words
     */
    @Test
    fun check_getWords() {
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

        val words = listOf(
            Word("cat", "кіт", languageInfo.locale),
            Word("dog", "пес", languageInfo.locale),
            Word("parrot", "папуга", languageInfo.locale),
            Word("turtle", "черепаха", languageInfo.locale)
        )

        every { wordDataSource.getWordsByLanguage(languageInfo.locale) } returns Single.just(words)

        every { viewInterface.showPlaceholder(any()) } just Runs

        every { viewInterface.setWords(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.init()

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            wordDataSource.getWordsByLanguage(languageInfo.locale)
            viewInterface.showPlaceholder(false)
            viewInterface.setWords(words)
            viewInterface.showProgress(false)
        }
    }

    /**
     * Given:
     * - getWordsByLanguage in wordDataSource returns empty list of words
     * When:
     * - getWords is called with default language
     * Then should:
     * - call getWordsByLanguage in wordDataSource with default language
     * - call showPlaceholder in viewInterface with true as isVisible
     */
    @Test
    fun check_getWords_emptyList() {
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

        every { wordDataSource.getWordsByLanguage(languageInfo.locale) } returns Single.just(emptyList())

        every { viewInterface.showPlaceholder(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.init()

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            wordDataSource.getWordsByLanguage(languageInfo.locale)
            viewInterface.showPlaceholder(true)
            viewInterface.showProgress(false)
        }
    }

    /**
     * Given:
     * - getWordsByLanguage in wordDataSource throws exception
     * When:
     * - getWords is called with default language
     * Then should:
     * - call getWordsByLanguage in wordDataSource with default language
     * - call showPlaceholder in viewInterface with true as isVisible
     * - call showMessage in viewInterface with R.string.general_error as resId
     */
    @Test
    fun check_getWords_error() {
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
        every { wordDataSource.getWordsByLanguage(languageInfo.locale) } returns Single.error(testException)

        every { viewInterface.showPlaceholder(any()) } just Runs

        every { viewInterface.showMessage(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        //When
        presenter.init()

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            wordDataSource.getWordsByLanguage(languageInfo.locale)
            viewInterface.showPlaceholder(true)
            viewInterface.showMessage(R.string.general_error)
            viewInterface.showProgress(false)
        }
    }

    /**
     * Given:
     * - insertWord in wordDataSource returns completable
     * When:
     * - addWordToDictionary is called with some word
     * Then should:
     * - call showMessage in viewInterface with R.string.word_added_successfully as resId
     * - call getWords with language from given word
     */
    @Test
    fun check_addWordToDictionary() {
        //Given
        val word = "cat"
        val translation = "кіт"

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

        every { wordDataSource.getWordsByLanguage(languageInfo.locale) } returns Single.just(emptyList())

        every { viewInterface.showPlaceholder(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        every { viewInterface.showMessage(any()) } just Runs

        presenter.init()

        every { wordDataSource.insertWord(any()) } returns Completable.complete()

        //When
        presenter.onSaveButtonClick(word, translation)

        //Then
        verify {
            wordDataSource.insertWord(Word(word, translation, languageInfo.locale))
            viewInterface.showMessage(R.string.word_added_successfully)
        }
    }

    /**
     * Given:
     * - insertWord in wordDataSource throws exception
     * When:
     * - addWordToDictionary is called with some word
     * Then should:
     * - call showMessage in viewInterface with R.string.general_error as resId
     */
    @Test
    fun check_addWordToDictionary_error() {
        //Given
        val word = "cat"
        val translation = "кіт"

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

        every { wordDataSource.getWordsByLanguage(languageInfo.locale) } returns Single.just(emptyList())

        every { viewInterface.showPlaceholder(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        every { viewInterface.showMessage(any()) } just Runs

        presenter.init()

        val testException = Exception("test exception")
        every { wordDataSource.insertWord(any()) } returns Completable.error(testException)

        //When
        presenter.onSaveButtonClick(word, translation)

        //Then
        verify {
            wordDataSource.insertWord(Word(word, translation, languageInfo.locale))
            viewInterface.showMessage(R.string.general_error)
        }
    }

    /**
     * Given:
     * - deleteWord in wordDataSource returns completable
     * When:
     * - deleteWordFromDictionary is called with some word
     * Then should:
     * - call showMessage in viewInterface with R.string.word_deleted_successfully as resId
     * - call getWords with language from given word
     */
    @Test
    fun check_deleteWordFromDictionary() {
        //Given
        val language = "en"

        val word = Word("cat", "кіт", language)

        val application: Application = mockk()
        val sharedPreferences: SharedPreferences = mockk(relaxed = true)

        every { application.packageName } returns "com.mayada1994.mydictionary_mvp"
        every { application.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.getString(CacheUtils.CACHED_DEFAULT_LANGUAGE, null) } returns language

        DictionaryComponent.init(application)

        every { wordDataSource.getWordsByLanguage(any()) } returns Single.just(emptyList())

        every { viewInterface.showProgress(any()) } just Runs

        every { viewInterface.showPlaceholder(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        every { viewInterface.showMessage(any()) } just Runs

        presenter.init()

        every { wordDataSource.deleteWord(any()) } returns Completable.complete()

        //When
        presenter.onDeleteButtonClick(word)

        //Then
        verify {
            wordDataSource.deleteWord(word)
            viewInterface.showMessage(R.string.word_deleted_successfully)
        }
    }

    /**
     * Given:
     * - deleteWord in wordDataSource throws exception
     * When:
     * - deleteWordFromDictionary is called with some word
     * Then should:
     * - call showMessage in viewInterface with R.string.general_error as resId
     */
    @Test
    fun check_deleteWordFromDictionary_error() {
        //Given
        val language = "en"

        val word = Word("cat", "кіт", language)

        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        val application: Application = mockk()
        val sharedPreferences: SharedPreferences = mockk(relaxed = true)

        every { application.packageName } returns "com.mayada1994.mydictionary_mvp"
        every { application.getSharedPreferences(any(), any()) } returns sharedPreferences
        every { sharedPreferences.getString(CacheUtils.CACHED_DEFAULT_LANGUAGE, null) } returns language

        DictionaryComponent.init(application)

        every { wordDataSource.getWordsByLanguage(any()) } returns Single.just(emptyList())

        every { viewInterface.showProgress(any()) } just Runs

        every { viewInterface.showPlaceholder(any()) } just Runs

        every { viewInterface.setToolbar(any()) } just Runs

        every { viewInterface.showMessage(any()) } just Runs

        presenter.init()

        val testException = Exception("test exception")
        every { wordDataSource.deleteWord(any()) } returns Completable.error(testException)

        //When
        presenter.onDeleteButtonClick(word)

        //Then
        verify {
            wordDataSource.deleteWord(word)
            viewInterface.showMessage(R.string.general_error)
        }
    }

}