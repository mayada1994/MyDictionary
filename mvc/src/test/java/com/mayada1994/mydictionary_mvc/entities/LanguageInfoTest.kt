package com.mayada1994.mydictionary_mvc.entities

import com.mayada1994.mydictionary_mvc.R
import com.mayada1994.mydictionary_mvc.items.DefaultLanguageItem
import com.mayada1994.mydictionary_mvc.items.LanguageItem
import org.junit.Assert.assertEquals
import org.junit.Test

class LanguageInfoTest {

    @Test
    fun `Given some locale, when toLanguage called, then should return Language with same locale`() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        //When
        val result = languageInfo.toLanguage()

        //Then
        assertEquals(Language(languageInfo.locale), result)
    }

    @Test
    fun `Given some nameRes, locale and imageRes, when toLanguageItem called, then should return LanguageItem with same data`() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        //When
        val result = languageInfo.toLanguageItem()

        //Then
        assertEquals(LanguageItem(languageInfo.nameRes, languageInfo.locale, languageInfo.imageRes), result)
    }

    @Test
    fun `Given some nameRes, locale and imageRes, when toDefaultLanguageItem called, then should return DefaultLanguageItem with same data`() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        //When
        val result = languageInfo.toDefaultLanguageItem()

        //Then
        assertEquals(DefaultLanguageItem(languageInfo.nameRes, languageInfo.locale, languageInfo.imageRes), result)
    }

}