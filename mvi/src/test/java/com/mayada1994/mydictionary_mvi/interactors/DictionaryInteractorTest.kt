package com.mayada1994.mydictionary_mvi.interactors

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.entities.Word
import com.mayada1994.mydictionary_mvi.repositories.WordRepository
import com.mayada1994.mydictionary_mvi.states.DictionaryState
import com.mayada1994.mydictionary_mvi.utils.CacheUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.Single
import org.junit.*

class DictionaryInteractorTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val wordRepository: WordRepository = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var interactor: DictionaryInteractor

    @Before
    fun setup() {
        interactor = DictionaryInteractor(wordRepository, cacheUtils)
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
     * - call getWords with languageInfo
     * - call getWordsByLanguage in wordRepository
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

        every { wordRepository.getWordsByLanguage(any()) } returns Single.just(emptyList())

        //When
        interactor.getData()

        //Then
        verify { wordRepository.getWordsByLanguage(languageInfo.locale) }
        Assert.assertEquals(languageInfo.locale,
            interactor::class.java.getDeclaredField("defaultLanguage").apply { isAccessible = true }
                .get(interactor) as String?
        )
    }

    /**
     * Given:
     * - defaultLanguage in cacheUtils returns null
     * When:
     * - getData is called
     * Then should:
     * - call getLanguages in LanguageUtils
     * - return DictionaryState.ErrorState with R.string.general_error as resId
     */
    @Test
    fun check_getData_nullDefaultLanguage() {
        //Given
        every { cacheUtils.defaultLanguage } returns null

        val state = DictionaryState.ErrorState(R.string.general_error)

        //When
        val result = interactor.getData()

        //Then
        result.test().assertResult(state)
    }

    /**
     * Given:
     * - getWordsByLanguage in wordRepository returns list of words
     * When:
     * - getWords is called with some languageInfo
     * Then should:
     * - return DictionaryState.DataState with given languageInfo and returned list of words
     */
    @Test
    fun check_getWords() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        val words = listOf(
            Word("cat", "кіт", languageInfo.locale),
            Word("dog", "пес", languageInfo.locale),
            Word("parrot", "папуга", languageInfo.locale),
            Word("turtle", "черепаха", languageInfo.locale)
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        every { wordRepository.getWordsByLanguage(any()) } returns Single.just(words)

        //When
        val result = interactor.getData()

        //Then
        Assert.assertEquals(DictionaryState.DataState::class, result.blockingFirst()::class)
    }

    /**
     * Given:
     * - getWordsByLanguage in wordRepository returns empty list of words
     * When:
     * - getWords is called with some languageInfo
     * Then should:
     * - return DictionaryState.EmptyState with given languageInfo
     */
    @Test
    fun check_getWords_empty() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        every { wordRepository.getWordsByLanguage(any()) } returns Single.just(emptyList())

        val state = DictionaryState.EmptyState(languageInfo)

        //When
        val result = interactor.getData()

        //Then
        result.test().assertResult(state)
    }

    /**
     * Given:
     * - getWordsByLanguage in wordRepository throws exception
     * When:
     * - getWords is called with some languageInfo
     * Then should:
     * - return DictionaryState.ErrorState with R.string.general_error as resId
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

        every { wordRepository.getWordsByLanguage(any()) } returns Single.error(testException)

        val state = DictionaryState.ErrorState(R.string.general_error)

        //When
        val result = interactor.getData()

        //Then
        result.test().assertResult(state)
    }

    @Test
    fun `When onAddButtonClick is called, then should return ShowAddNewWordDialogState`() {
        //Given
        val state = DictionaryState.ShowAddNewWordDialogState

        //When
        val result = interactor.onAddButtonClick()

        //Then
        result.test().assertResult(state)
    }

    @Test
    fun `When onSaveButtonClick is called with null as word, then should return CompletedState with fill all fields prompt`() {
        //Given
        val word: String? = null
        val translation = "кіт"

        val state = DictionaryState.CompletedState(R.string.fill_all_fields_prompt)

        //When
        val result = interactor.onSaveButtonClick(word, translation)

        //Then
        result.test().assertResult(state)
    }

    @Test
    fun `When onSaveButtonClick is called with null as translation, then should return CompletedState with fill all fields prompt`() {
        //Given
        val word = "cat"
        val translation: String? = null

        val state = DictionaryState.CompletedState(R.string.fill_all_fields_prompt)

        //When
        val result = interactor.onSaveButtonClick(word, translation)

        //Then
        result.test().assertResult(state)
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

        interactor.getData()

        every { wordRepository.insertWord(any()) } returns Single.just(Unit)

        //When
        interactor.onSaveButtonClick(word, translation)

        //Then
        verify { wordRepository.insertWord(Word(word, translation, languageInfo.locale)) }
    }

    @Test
    fun `Given defaultLanguage is null, when onSaveButtonClick is called with word and translation, then should return ErrorState with general error`() {
        //Given
        val word = "cat"
        val translation = "кіт"

       val state = DictionaryState.ErrorState(R.string.general_error)

        //When
        val result = interactor.onSaveButtonClick(word, translation)

        //Then
        result.test().assertResult(state)
    }

    @Test
    fun `When addWordToDictionary is called with some word, then should call getWords and return DataState`() {
        //Given
        val word = Word("cat", "кіт", "en")

        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        every { wordRepository.getWordsByLanguage(languageInfo.locale) } returns Single.just(listOf(word))

        interactor.getData()

        every { wordRepository.insertWord(any()) } returns Single.just(Unit)

        val state = DictionaryState.DataState(languageInfo, listOf(word), R.string.word_added_successfully)

        //When
        val result = interactor.onSaveButtonClick(word.name, word.translation)

        //Then
        result.test().assertResult(state)
    }

    @Test
    fun `When addWordToDictionary is called with some word in unavailable language, then should return CompletedState`() {
        //Given
        val word = Word("cat", "кіт", "eg")

        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "eg",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        every { wordRepository.getWordsByLanguage(languageInfo.locale) } returns Single.just(listOf(word))

        interactor.getData()

        every { wordRepository.insertWord(any()) } returns Single.just(Unit)

        val state = DictionaryState.CompletedState(R.string.word_added_successfully)

        //When
        val result = interactor.onSaveButtonClick(word.name, word.translation)

        //Then
        result.test().assertResult(state)
    }

    @Test
    fun `Given insertWord in wordRepository throws exception, when addWordToDictionary is called with some word, then should return CompletedState with general error`() {
        //Given
        val word = Word("cat", "кіт", "en")

        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { cacheUtils.defaultLanguage } returns languageInfo.locale

        every { wordRepository.getWordsByLanguage(languageInfo.locale) } returns Single.just(listOf(word))

        interactor.getData()

        val testException = Exception("test exception")
        every { wordRepository.insertWord(any()) } returns Single.error(testException)

        val state = DictionaryState.CompletedState(R.string.general_error)

        //When
        val result = interactor.onSaveButtonClick(word.name, word.translation)

        //Then
        result.test().assertResult(state)
    }

    @Test
    fun `When deleteWordFromDictionary is called with some word, then should call getWords and return DataState`() {
        //Given
        val word = Word("cat", "кіт", "en")

        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { wordRepository.getWordsByLanguage(languageInfo.locale) } returns Single.just(listOf(word))

        every { wordRepository.deleteWord(any()) } returns Single.just(Unit)

        val state = DictionaryState.DataState(languageInfo, listOf(word), R.string.word_deleted_successfully)

        //When
        val result = interactor.deleteWordFromDictionary(word)

        //Then
        result.test().assertResult(state)
    }

    @Test
    fun `When deleteWordFromDictionary is called with some word in unavailable language, then should return CompletedState`() {
        //Given
        val word = Word("cat", "кіт", "eg")

        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "eg",
            imageRes = R.drawable.ic_england
        )

        every { wordRepository.getWordsByLanguage(languageInfo.locale) } returns Single.just(listOf(word))

        every { wordRepository.deleteWord(any()) } returns Single.just(Unit)

        val state = DictionaryState.CompletedState(R.string.word_deleted_successfully)

        //When
        val result = interactor.deleteWordFromDictionary(word)

        //Then
        result.test().assertResult(state)
    }

    @Test
    fun `Given insertWord in wordRepository throws exception, when deleteWordFromDictionary is called with some word, then should return CompletedState with general error`() {
        //Given
        val word = Word("cat", "кіт", "en")

        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        every { wordRepository.getWordsByLanguage(languageInfo.locale) } returns Single.just(listOf(word))

        val testException = Exception("test exception")
        every { wordRepository.deleteWord(any()) } returns Single.error(testException)

        val state = DictionaryState.CompletedState(R.string.general_error)

        //When
        val result = interactor.deleteWordFromDictionary(word)

        //Then
        result.test().assertResult(state)
    }

}