package com.mayada1994.mydictionary_mvp.items

import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import org.junit.Assert.assertEquals
import org.junit.Test

class LanguageItemTest {

    @Test
    fun `Given some nameRes, locale and imageRes, when toLanguageInfo called, then should return LanguageInfo with same data`() {
        //Given
        val languageItem = LanguageItem(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        //When
        val result = languageItem.toLanguageInfo()

        //Then
        assertEquals(LanguageInfo(languageItem.nameRes, languageItem.locale, languageItem.imageRes), result)
    }

}