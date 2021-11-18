package com.mayada1994.mydictionary_mvi.utils

import android.content.SharedPreferences
import androidx.core.content.edit

class CacheUtils(private val sharedPreferences: SharedPreferences) {

    var defaultLanguage: String?
        get() = sharedPreferences.getString(CACHED_DEFAULT_LANGUAGE, null)
        set(language) = sharedPreferences.edit { putString(CACHED_DEFAULT_LANGUAGE, language) }

    companion object {
        const val CACHED_DEFAULT_LANGUAGE = "cached_locale"
    }

}