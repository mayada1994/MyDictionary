package com.mayada1994.mydictionary_mvvm.entities

import com.mayada1994.mydictionary_mvvm.items.DefaultLanguageItem
import com.mayada1994.mydictionary_mvvm.items.LanguageItem

data class LanguageInfo(
    val nameRes: Int,
    val locale: String,
    val imageRes: Int
) {
    fun toLanguage(): Language = Language(code = locale)

    fun toLanguageItem(): LanguageItem = LanguageItem(nameRes, locale, imageRes)

    fun toDefaultLanguageItem(): DefaultLanguageItem = DefaultLanguageItem(nameRes, locale, imageRes)
}