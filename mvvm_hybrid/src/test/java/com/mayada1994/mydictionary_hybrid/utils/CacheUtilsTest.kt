package com.mayada1994.mydictionary_hybrid.utils

import android.content.SharedPreferences
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CacheUtilsTest {

    private val sharedPreferences: SharedPreferences = mockk(relaxed = true)

    private val editor: SharedPreferences.Editor = mockk(relaxed = true)

    private lateinit var cacheUtils: CacheUtils

    @Before
    fun setup() {
        cacheUtils = CacheUtils(sharedPreferences)

        every { sharedPreferences.edit() } returns editor
    }

    @After
    fun clear() {
        unmockkAll()
    }

    @Test
    fun `When getDefaultLanguage is called, then should return default language value`() {
        //Given
        val defaultLanguage = "en"
        every { sharedPreferences.getString(CacheUtils.CACHED_DEFAULT_LANGUAGE, null) } returns defaultLanguage

        //When
        val result = cacheUtils.defaultLanguage

        //Then
        verify { sharedPreferences.getString(CacheUtils.CACHED_DEFAULT_LANGUAGE, null) }

        assertEquals(defaultLanguage, result)
    }

    @Test
    fun `When setDefaultLanguage is called with some default language, then should cache it as default language`() {
        //Given
        val defaultLanguage = "en"
        every { editor.putString(CacheUtils.CACHED_DEFAULT_LANGUAGE, defaultLanguage).apply() } just Runs

        //When
        cacheUtils.defaultLanguage = defaultLanguage

        //Then
        verify { editor.putString(CacheUtils.CACHED_DEFAULT_LANGUAGE, defaultLanguage) }
    }

}