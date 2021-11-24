package com.mayada1994.mydictionary_hybrid.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.mydictionary_hybrid.R
import com.mayada1994.mydictionary_hybrid.entities.LanguageInfo
import com.mayada1994.mydictionary_hybrid.entities.Word
import com.mayada1994.mydictionary_hybrid.repositories.WordRepository
import com.mayada1994.mydictionary_hybrid.utils.CacheUtils
import com.mayada1994.mydictionary_hybrid.utils.ViewEvent
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

    private val observerViewEvent: Observer<ViewEvent> = mockk()

    private val wordRepository: WordRepository = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var viewModel: DictionaryViewModel

    @Before
    fun setup() {
        viewModel = DictionaryViewModel(wordRepository, cacheUtils)
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
     * - set private field defaultLanguage value as en
     * - call getWords with default language
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

        every { wordRepository.getWordsByLanguage(languageInfo.locale) } returns Single.just(emptyList())

        //When
        viewModel.init()

        //Then
        verifyOrder {
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(true))
            wordRepository.getWordsByLanguage(languageInfo.locale)
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowPlaceholder(true))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(false))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.SetDefaultLanguage(languageInfo))
        }
    }

    @Test
    fun `When onAddButtonClick is called, then should call setEvent with ShowAddNewWordDialog`() {
        //When
        viewModel.onAddButtonClick()

        //Then
        verify { observerViewEvent.onChanged(DictionaryViewModel.DictionaryEvent.ShowAddNewWordDialog) }
    }

    @Test
    fun `When onSaveButtonClick is called with null as word, then should call setEvent with ShowMessage with fill all fields prompt`() {
        //Given
        val word: String? = null
        val translation = "кіт"

        //When
        viewModel.onSaveButtonClick(word, translation)

        //Then
        verify { observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowMessage(R.string.fill_all_fields_prompt)) }
    }

    @Test
    fun `When onSaveButtonClick is called with null as translation, then should call setEvent with ShowMessage with fill all fields prompt`() {
        //Given
        val word = "cat"
        val translation: String? = null

        //When
        viewModel.onSaveButtonClick(word, translation)

        //Then
        verify { observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowMessage(R.string.fill_all_fields_prompt)) }
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
     * - call setEvent with ShowPlaceholder with false as isVisible
     * - call setEvent with SetWords with list of words
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
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(true))
            wordRepository.getWordsByLanguage(languageInfo.locale)
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowPlaceholder(false))
            observerViewEvent.onChanged(DictionaryViewModel.DictionaryEvent.SetWords(words))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(false))
        }
    }

    /**
     * Given:
     * - getWordsByLanguage in wordRepository returns empty list of words
     * When:
     * - getWords is called with default language
     * Then should:
     * - call getWordsByLanguage in wordRepository with default language
     * - call setEvent with ShowPlaceholder with true as isVisible
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
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(true))
            wordRepository.getWordsByLanguage(languageInfo.locale)
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowPlaceholder(true))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(false))
        }
    }

    /**
     * Given:
     * - getWordsByLanguage in wordRepository throws exception
     * When:
     * - getWords is called with default language
     * Then should:
     * - call getWordsByLanguage in wordRepository with default language
     * - call setEvent with ShowPlaceholder with true as isVisible
     * - call setEvent with ShowMessage with R.string.general_error as resId
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
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(true))
            wordRepository.getWordsByLanguage(languageInfo.locale)
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowPlaceholder(true))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowMessage(R.string.general_error))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(false))
        }
    }

    /**
     * Given:
     * - insertWord in wordRepository returns completable
     * When:
     * - addWordToDictionary is called with some word
     * Then should:
     * - call setEvent with ShowMessage with R.string.word_added_successfully as resId
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
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowMessage(R.string.word_added_successfully))
        }
    }

    /**
     * Given:
     * - insertWord in wordRepository throws exception
     * When:
     * - addWordToDictionary is called with some word
     * Then should:
     * - call setEvent with ShowMessage with R.string.general_error as resId
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
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowMessage(R.string.general_error))
        }
    }

    /**
     * Given:
     * - deleteWord in wordRepository returns completable
     * When:
     * - deleteWordFromDictionary is called with some word
     * Then should:
     * - call setEvent with ShowMessage with R.string.word_deleted_successfully as resId
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
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowMessage(R.string.word_deleted_successfully))
        }
    }

    /**
     * Given:
     * - deleteWord in wordRepository throws exception
     * When:
     * - deleteWordFromDictionary is called with some word
     * Then should:
     * - call setEvent with ShowMessage with R.string.general_error as resId
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
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowMessage(R.string.general_error))
        }
    }
    
}