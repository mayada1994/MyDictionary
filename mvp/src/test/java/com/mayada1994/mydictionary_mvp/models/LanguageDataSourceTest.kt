package com.mayada1994.mydictionary_mvp.models

import com.mayada1994.mydictionary_mvp.db.dao.LanguageDao
import com.mayada1994.mydictionary_mvp.entities.Language
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

class LanguageDataSourceTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val languageDao: LanguageDao = mockk()

    private lateinit var languageDataSource: LanguageDataSource

    @Before
    fun setup() {
        languageDataSource = LanguageDataSource(languageDao)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    @Test
    fun `When getLanguages is called, then should return some list of languages`() {
        //Given
        val languages = listOf(
            Language("en"),
            Language("fr"),
            Language("de")
        )

        every { languageDao.getLanguages() } returns Single.just(languages)

        //When
        val result = languageDataSource.getLanguages()

        //Then
        result.test().assertValue(languages)
        verify { languageDao.getLanguages() }
    }

    @Test
    fun `When insertLanguages is called with some list of languages, then should save the list to DB`() {
        //Given
        val languages = listOf(
            Language("en"),
            Language("fr"),
            Language("de")
        )

        every { languageDao.insertLanguages(languages) } returns Completable.complete()

        //When
        val result = languageDataSource.insertLanguages(languages)

        //Then
        result.test().assertComplete()
        verify { languageDao.insertLanguages(languages) }
    }

}