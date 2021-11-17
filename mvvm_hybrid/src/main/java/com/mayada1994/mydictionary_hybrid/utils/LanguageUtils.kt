package com.mayada1994.mydictionary_hybrid.utils

import com.mayada1994.mydictionary_hybrid.R
import com.mayada1994.mydictionary_hybrid.entities.LanguageInfo

object LanguageUtils {

    private val languages = listOf(
        LanguageInfo(
            nameRes = R.string.arabic_language,
            locale = "ar",
            imageRes = R.drawable.ic_uae
        ),
        LanguageInfo(
            nameRes = R.string.chinese_language,
            locale = "zh",
            imageRes = R.drawable.ic_china
        ),
        LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        ),
        LanguageInfo(
            nameRes = R.string.french_language,
            locale = "fr",
            imageRes = R.drawable.ic_france
        ),
        LanguageInfo(
            nameRes = R.string.german_language,
            locale = "de",
            imageRes = R.drawable.ic_germany
        ),
        LanguageInfo(
            nameRes = R.string.italian_language,
            locale = "it",
            imageRes = R.drawable.ic_italy
        ),
        LanguageInfo(
            nameRes = R.string.japanese_language,
            locale = "ja",
            imageRes = R.drawable.ic_japan
        ),
        LanguageInfo(
            nameRes = R.string.korean_language,
            locale = "ko",
            imageRes = R.drawable.ic_korea
        ),
        LanguageInfo(
            nameRes = R.string.polish_language,
            locale = "pl",
            imageRes = R.drawable.ic_poland
        ),
        LanguageInfo(
            nameRes = R.string.spanish_language,
            locale = "es",
            imageRes = R.drawable.ic_spain
        ),
        LanguageInfo(
            nameRes = R.string.turkish_language,
            locale = "tr",
            imageRes = R.drawable.ic_turkey
        )
    )

    val languagesTotal: Int = languages.size

    fun getLanguageByCode(code: String): LanguageInfo? = languages.find { it.locale == code }

    fun getLanguages(): List<LanguageInfo> = languages

}