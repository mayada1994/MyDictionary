package com.mayada1994.mydictionary.items

import com.mayada1994.mydictionary.entities.LanguageInfo

data class LanguageItem(
    val nameRes: Int,
    val locale: String,
    val imageRes: Int,
    var isSelected: Boolean = false
) {
    fun toLanguageInfo(): LanguageInfo = LanguageInfo(nameRes, locale, imageRes)
}