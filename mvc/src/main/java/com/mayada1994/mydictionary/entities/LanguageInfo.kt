package com.mayada1994.mydictionary.entities

import com.mayada1994.mydictionary.items.DefaultLanguageItem
import com.mayada1994.mydictionary.items.LanguageItem

data class LanguageInfo(
    val nameRes: Int,
    val locale: String,
    val imageRes: Int
) {
    fun toLanguage(): Language = Language(code = locale)

    fun toLanguageItem(): LanguageItem = LanguageItem(nameRes, locale, imageRes)

    fun toDefaultLanguageItem(): DefaultLanguageItem = DefaultLanguageItem(nameRes, locale, imageRes)
}