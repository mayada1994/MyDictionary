package com.mayada1994.mydictionary_mvi.items

data class DefaultLanguageItem(
    val nameRes: Int,
    val locale: String,
    val imageRes: Int,
    var isDefault: Boolean = false
)