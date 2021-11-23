package com.mayada1994.mydictionary_mvvm.repositories

import com.mayada1994.mydictionary_mvvm.db.dao.WordDao
import com.mayada1994.mydictionary_mvvm.entities.Word
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WordRepositoryTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val wordDao: WordDao = mockk()

    private lateinit var wordRepository: WordRepository

    @Before
    fun setup() {
        wordRepository = WordRepository(wordDao)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    @Test
    fun `When getWordsByLanguage is called with some language, then should return some list of words for that language`() {
        //Given
        val language = "en"
        val words = listOf(
            Word("cat", "кіт", language),
            Word("dog", "пес", language),
            Word("parrot", "папуга", language)
        )

        every { wordDao.getWordsByLanguage(language) } returns Single.just(words)

        //When
        val result = wordRepository.getWordsByLanguage(language)

        //Then
        result.test().assertValue(words)
        verify { wordDao.getWordsByLanguage(language) }
    }

    @Test
    fun `When insertWord is called with some word, then should save it to DB`() {
        //Given
        val word = Word("parrot", "папуга", "en")

        every { wordDao.insertWord(word) } returns Completable.complete()

        //When
        val result = wordRepository.insertWord(word)

        //Then
        result.test().assertComplete()
        verify { wordDao.insertWord(word) }
    }

    @Test
    fun `When deleteWord is called with some word, then should delete it from DB`() {
        //Given
        val word = Word("parrot", "папуга", "en")

        every { wordDao.deleteWord(word) } returns Completable.complete()

        //When
        val result = wordRepository.deleteWord(word)

        //Then
        result.test().assertComplete()
        verify { wordDao.deleteWord(word) }
    }

}