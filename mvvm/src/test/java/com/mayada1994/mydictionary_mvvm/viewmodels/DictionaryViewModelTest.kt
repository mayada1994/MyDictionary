package com.mayada1994.mydictionary_mvvm.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.mydictionary_mvvm.R
import com.mayada1994.mydictionary_mvvm.entities.LanguageInfo
import com.mayada1994.mydictionary_mvvm.entities.Word
import com.mayada1994.mydictionary_mvvm.repositories.WordRepository
import com.mayada1994.mydictionary_mvvm.utils.CacheUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class DictionaryViewModelTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val observerDefaultLanguage: Observer<LanguageInfo> = mockk()
    private val observerWordsList: Observer<List<Word>> = mockk()
    private val observerShowAddNewWordDialog: Observer<Boolean> = mockk()
    private val observerIsPlaceholderVisible: Observer<Boolean> = mockk()
    private val observerIsProgressVisible: Observer<Boolean> = mockk()
    private val observerToastMessageStringResId: Observer<Int> = mockk()

    private val wordRepository: WordRepository = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var viewModel: DictionaryViewModel

    @Before
    fun setup() {
        viewModel = DictionaryViewModel(wordRepository, cacheUtils)
        viewModel.defaultLanguage.observeForever(observerDefaultLanguage)
        viewModel.wordsList.observeForever(observerWordsList)
        viewModel.showAddNewWordDialog.observeForever(observerShowAddNewWordDialog)
        viewModel.isPlaceholderVisible.observeForever(observerIsPlaceholderVisible)
        viewModel.isProgressVisible.observeForever(observerIsProgressVisible)
        viewModel.toastMessageStringResId.observeForever(observerToastMessageStringResId)
        every { observerDefaultLanguage.onChanged(any()) } just Runs
        every { observerWordsList.onChanged(any()) } just Runs
        every { observerShowAddNewWordDialog.onChanged(any()) } just Runs
        every { observerIsPlaceholderVisible.onChanged(any()) } just Runs
        every { observerIsProgressVisible.onChanged(any()) } just Runs
        every { observerToastMessageStringResId.onChanged(any()) } just Runs
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
     * - set private field defaultLanguage value as en
     * - call getWords with default language
     * - call getLanguageByCode in LanguageUtils which returns default languageInfo
     * - post defaultLanguage with default languageInfo
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

        every { wordRepository.getWordsByLanguage(languageInfo.locale) } returns Single.just(emptyList())

        //When
        viewModel.init()

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            wordRepository.getWordsByLanguage(languageInfo.locale)
            observerIsPlaceholderVisible.onChanged(true)
            observerIsProgressVisible.onChanged(false)
            observerDefaultLanguage.onChanged(languageInfo)
        }
    }

    @Test
    fun `When onAddButtonClick is called, then should post showAddNewWordDialog`() {
        //When
        viewModel.onAddButtonClick()

        //Then
        verify { observerShowAddNewWordDialog.onChanged(true) }
    }

    @Test
    fun `When onSaveButtonClick is called with null as word, then should post toastMessageStringResId with fill all fields prompt`() {
        //Given
        val word: String? = null
        val translation = "кіт"

        //When
        viewModel.onSaveButtonClick(word, translation)

        //Then
        verify { observerToastMessageStringResId.onChanged(R.string.fill_all_fields_prompt) }
    }

    @Test
    fun `When onSaveButtonClick is called with null as translation, then should post toastMessageStringResId with fill all fields prompt`() {
        //Given
        val word = "cat"
        val translation: String? = null

        //When
        viewModel.onSaveButtonClick(word, translation)

        //Then
        verify { observerToastMessageStringResId.onChanged(R.string.fill_all_fields_prompt) }
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

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        every { wordRepository.getWordsByLanguage(languageInfo.locale) } returns Single.just(emptyList())

        viewModel.init()

        every { wordRepository.insertWord(any()) } returns Completable.complete()

        //When
        viewModel.onSaveButtonClick(word, translation)

        //Then
        verify { wordRepository.insertWord(Word(word, translation, languageInfo.locale)) }
    }

    @Test
    fun `When onDeleteButtonClick is called with some word, then should call deleteWordFromDictionary with given word`() {
        //Given
        val word = Word("cat", "кіт", "en")

        every { wordRepository.deleteWord(any()) } returns Completable.complete()

        //When
        viewModel.onDeleteButtonClick(word)

        //Then
        verify { wordRepository.deleteWord(word) }
    }

    /**
     * Given:
     * - getWordsByLanguage in wordRepository returns list of words
     * When:
     * - getWords is called with default language
     * Then should:
     * - call getWordsByLanguage in wordRepository with default language
     * - post isPlaceholderVisible with false as isVisible
     * - post wordsList with list of words
     */
    @Test
    fun check_getWords() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        val words = listOf(
            Word("cat", "кіт", languageInfo.locale),
            Word("dog", "пес", languageInfo.locale),
            Word("parrot", "папуга", languageInfo.locale),
            Word("turtle", "черепаха", languageInfo.locale)
        )

        every { wordRepository.getWordsByLanguage(languageInfo.locale) } returns Single.just(words)

        //When
        viewModel.init()

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            wordRepository.getWordsByLanguage(languageInfo.locale)
            observerIsPlaceholderVisible.onChanged(false)
            observerWordsList.onChanged(words)
            observerIsProgressVisible.onChanged(false)
        }
    }

    /**
     * Given:
     * - getWordsByLanguage in wordRepository returns empty list of words
     * When:
     * - getWords is called with default language
     * Then should:
     * - call getWordsByLanguage in wordRepository with default language
     * - post isPlaceholderVisible with true as isVisible
     */
    @Test
    fun check_getWords_emptyList() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        every { wordRepository.getWordsByLanguage(languageInfo.locale) } returns Single.just(emptyList())

        //When
        viewModel.init()

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            wordRepository.getWordsByLanguage(languageInfo.locale)
            observerIsPlaceholderVisible.onChanged(true)
            observerIsProgressVisible.onChanged(false)
        }
    }

    /**
     * Given:
     * - getWordsByLanguage in wordRepository throws exception
     * When:
     * - getWords is called with default language
     * Then should:
     * - call getWordsByLanguage in wordRepository with default language
     * - post isPlaceholderVisible with true as isVisible
     * - post toastMessageStringResId with R.string.general_error as resId
     */
    @Test
    fun check_getWords_error() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        val testException = Exception("test exception")
        every { wordRepository.getWordsByLanguage(languageInfo.locale) } returns Single.error(testException)

        //When
        viewModel.init()

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            wordRepository.getWordsByLanguage(languageInfo.locale)
            observerIsPlaceholderVisible.onChanged(true)
            observerToastMessageStringResId.onChanged(R.string.general_error)
            observerIsProgressVisible.onChanged(false)
        }
    }

    /**
     * Given:
     * - insertWord in wordRepository returns completable
     * When:
     * - addWordToDictionary is called with some word
     * Then should:
     * - post toastMessageStringResId with R.string.word_added_successfully as resId
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

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        every { wordRepository.getWordsByLanguage(languageInfo.locale) } returns Single.just(emptyList())

        viewModel.init()

        every { wordRepository.insertWord(any()) } returns Completable.complete()

        //When
        viewModel.onSaveButtonClick(word, translation)

        //Then
        verify {
            wordRepository.insertWord(Word(word, translation, languageInfo.locale))
            observerToastMessageStringResId.onChanged(R.string.word_added_successfully)
        }
    }

    /**
     * Given:
     * - insertWord in wordRepository throws exception
     * When:
     * - addWordToDictionary is called with some word
     * Then should:
     * - post toastMessageStringResId with R.string.general_error as resId
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

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        every { wordRepository.getWordsByLanguage(languageInfo.locale) } returns Single.just(emptyList())

        viewModel.init()

        val testException = Exception("test exception")
        every { wordRepository.insertWord(any()) } returns Completable.error(testException)

        //When
        viewModel.onSaveButtonClick(word, translation)

        //Then
        verify {
            wordRepository.insertWord(Word(word, translation, languageInfo.locale))
            observerToastMessageStringResId.onChanged(R.string.general_error)
        }
    }

    /**
     * Given:
     * - deleteWord in wordRepository returns completable
     * When:
     * - deleteWordFromDictionary is called with some word
     * Then should:
     * - post toastMessageStringResId with R.string.word_deleted_successfully as resId
     * - call getWords with language from given word
     */
    @Test
    fun check_deleteWordFromDictionary() {
        //Given
        val language = "en"

        val word = Word("cat", "кіт", language)

        every { cacheUtils.defaultLanguage } returns language

        every { wordRepository.getWordsByLanguage(any()) } returns Single.just(emptyList())

        viewModel.init()

        every { wordRepository.deleteWord(any()) } returns Completable.complete()

        //When
        viewModel.onDeleteButtonClick(word)

        //Then
        verify {
            wordRepository.deleteWord(word)
            observerToastMessageStringResId.onChanged(R.string.word_deleted_successfully)
        }
    }

    /**
     * Given:
     * - deleteWord in wordRepository throws exception
     * When:
     * - deleteWordFromDictionary is called with some word
     * Then should:
     * - post toastMessageStringResId with R.string.general_error as resId
     */
    @Test
    fun check_deleteWordFromDictionary_error() {
        //Given
        val language = "en"

        val word = Word("cat", "кіт", language)

        every { cacheUtils.defaultLanguage } returns language

        every { wordRepository.getWordsByLanguage(any()) } returns Single.just(emptyList())

        viewModel.init()

        val testException = Exception("test exception")
        every { wordRepository.deleteWord(any()) } returns Completable.error(testException)

        //When
        viewModel.onDeleteButtonClick(word)

        //Then
        verify {
            wordRepository.deleteWord(word)
            observerToastMessageStringResId.onChanged(R.string.general_error)
        }
    }
    
}