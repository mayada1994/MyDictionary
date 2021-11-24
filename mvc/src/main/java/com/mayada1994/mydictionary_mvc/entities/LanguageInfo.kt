package com.mayada1994.mydictionary_mvc.entities

import com.mayada1994.mydictionary_mvc.items.DefaultLanguageItem
import com.mayada1994.mydictionary_mvc.items.LanguageItem

data class LanguageInfo(
    val nameRes: Int,
    val locale: String,
    val imageRes: Int
) {
    fun toLanguage(): Language = Language(code = locale)

    fun toLanguageItem(): LanguageItem = LanguageItem(nameRes, locale, imageRes)

    fun toDefaultLanguageItem(): DefaultLanguageItem = DefaultLanguageItem(nameRes, locale, imageRes)
}