package com.mayada1994.mydictionary_mvvm.items

data class DefaultLanguageItem(
    val nameRes: Int,
    val locale: String,
    val imageRes: Int,
    var isDefault: Boolean = false
)