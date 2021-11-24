package com.mayada1994.mydictionary_mvc.items

data class DefaultLanguageItem(
    val nameRes: Int,
    val locale: String,
    val imageRes: Int,
    var isDefault: Boolean = false
)