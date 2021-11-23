package com.mayada1994.mydictionary_mvi.utils

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import org.junit.Assert.assertEquals
import org.junit.Test

class LanguageUtilsTest {

    private val testLanguages = listOf(
        LanguageInfo(
            nameRes = R.string.arabic_language,
            locale = "ar",
            imageRes = R.drawable.ic_uae
        ),
        LanguageInfo(
            nameRes = R.string.chinese_language,
            locale = "zh",
            imageRes = R.drawable.ic_china
        ),
        LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        ),
        LanguageInfo(
            nameRes = R.string.french_language,
            locale = "fr",
            imageRes = R.drawable.ic_france
        ),
        LanguageInfo(
            nameRes = R.string.german_language,
            locale = "de",
            imageRes = R.drawable.ic_germany
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
            nameRes = R.string.korean_language,
            locale = "ko",
            imageRes = R.drawable.ic_korea
        ),
        LanguageInfo(
            nameRes = R.string.polish_language,
            locale = "pl",
            imageRes = R.drawable.ic_poland
        ),
        LanguageInfo(
            nameRes = R.string.spanish_language,
            locale = "es",
            imageRes = R.drawable.ic_spain
        ),
        LanguageInfo(
            nameRes = R.string.turkish_language,
            locale = "tr",
            imageRes = R.drawable.ic_turkey
        )
    )

    @Test
    fun `When languagesTotal is called, then should return size of the list of available languages`() {
        //When
        val result = LanguageUtils.languagesTotal

        //Then
        assertEquals(testLanguages.size, result)
    }

    @Test
    fun `When getLanguageByCode is called with ar as code, then should return corresponding LanguageInfo`() {
        //Given
        val code = "ar"

        val languageInfo = LanguageInfo(
            nameRes = R.string.arabic_language,
            locale = "ar",
            imageRes = R.drawable.ic_uae
        )

        //When
        val result = LanguageUtils.getLanguageByCode(code)

        //Then
        assertEquals(languageInfo, result)
    }

    @Test
    fun `When getLanguageByCode is called with zh as code, then should return corresponding LanguageInfo`() {
        //Given
        val code = "zh"

        val languageInfo = LanguageInfo(
            nameRes = R.string.chinese_language,
            locale = "zh",
            imageRes = R.drawable.ic_china
        )

        //When
        val result = LanguageUtils.getLanguageByCode(code)

        //Then
        assertEquals(languageInfo, result)
    }

    @Test
    fun `When getLanguageByCode is called with en as code, then should return corresponding LanguageInfo`() {
        //Given
        val code = "en"

        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        //When
        val result = LanguageUtils.getLanguageByCode(code)

        //Then
        assertEquals(languageInfo, result)
    }

    @Test
    fun `When getLanguageByCode is called with fr as code, then should return corresponding LanguageInfo`() {
        //Given
        val code = "fr"

        val languageInfo = LanguageInfo(
            nameRes = R.string.french_language,
            locale = "fr",
            imageRes = R.drawable.ic_france
        )

        //When
        val result = LanguageUtils.getLanguageByCode(code)

        //Then
        assertEquals(languageInfo, result)
    }

    @Test
    fun `When getLanguageByCode is called with de as code, then should return corresponding LanguageInfo`() {
        //Given
        val code = "de"

        val languageInfo = LanguageInfo(
            nameRes = R.string.german_language,
            locale = "de",
            imageRes = R.drawable.ic_germany
        )

        //When
        val result = LanguageUtils.getLanguageByCode(code)

        //Then
        assertEquals(languageInfo, result)
    }

    @Test
    fun `When getLanguageByCode is called with it as code, then should return corresponding LanguageInfo`() {
        //Given
        val code = "it"

        val languageInfo = LanguageInfo(
            nameRes = R.string.italian_language,
            locale = "it",
            imageRes = R.drawable.ic_italy
        )

        //When
        val result = LanguageUtils.getLanguageByCode(code)

        //Then
        assertEquals(languageInfo, result)
    }

    @Test
    fun `When getLanguageByCode is called with ja as code, then should return corresponding LanguageInfo`() {
        //Given
        val code = "ja"

        val languageInfo = LanguageInfo(
            nameRes = R.string.japanese_language,
            locale = "ja",
            imageRes = R.drawable.ic_japan
        )

        //When
        val result = LanguageUtils.getLanguageByCode(code)

        //Then
        assertEquals(languageInfo, result)
    }

    @Test
    fun `When getLanguageByCode is called with ko as code, then should return corresponding LanguageInfo`() {
        //Given
        val code = "ko"

        val languageInfo = LanguageInfo(
            nameRes = R.string.korean_language,
            locale = "ko",
            imageRes = R.drawable.ic_korea
        )

        //When
        val result = LanguageUtils.getLanguageByCode(code)

        //Then
        assertEquals(languageInfo, result)
    }

    @Test
    fun `When getLanguageByCode is called with pl as code, then should return corresponding LanguageInfo`() {
        //Given
        val code = "pl"

        val languageInfo = LanguageInfo(
            nameRes = R.string.polish_language,
            locale = "pl",
            imageRes = R.drawable.ic_poland
        )

        //When
        val result = LanguageUtils.getLanguageByCode(code)

        //Then
        assertEquals(languageInfo, result)
    }

    @Test
    fun `When getLanguageByCode is called with es as code, then should return corresponding LanguageInfo`() {
        //Given
        val code = "es"

        val languageInfo = LanguageInfo(
            nameRes = R.string.spanish_language,
            locale = "es",
            imageRes = R.drawable.ic_spain
        )

        //When
        val result = LanguageUtils.getLanguageByCode(code)

        //Then
        assertEquals(languageInfo, result)
    }

    @Test
    fun `When getLanguageByCode is called with tr as code, then should return corresponding LanguageInfo`() {
        //Given
        val code = "tr"

        val languageInfo = LanguageInfo(
            nameRes = R.string.turkish_language,
            locale = "tr",
            imageRes = R.drawable.ic_turkey
        )

        //When
        val result = LanguageUtils.getLanguageByCode(code)

        //Then
        assertEquals(languageInfo, result)
    }

    @Test
    fun `When getLanguageByCode is called with uk as code, then should return null`() {
        //Given
        val code = "uk"

        //When
        val result = LanguageUtils.getLanguageByCode(code)

        //Then
        assertEquals(null, result)
    }

    @Test
    fun `When getLanguages is called, then should return list of available languages`() {
        //When
        val result = LanguageUtils.getLanguages()

        //Then
        assertEquals(testLanguages, result)
    }

}