package com.mayada1994.mydictionary.items

data class DefaultLanguageItem(
    val nameRes: Int,
    val locale: String,
    val imageRes: Int,
    var isDefault: Boolean = false
)