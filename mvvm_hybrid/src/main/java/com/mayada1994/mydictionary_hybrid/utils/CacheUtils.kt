package com.mayada1994.mydictionary_hybrid.utils

import android.content.SharedPreferences
import androidx.core.content.edit

class CacheUtils(private val sharedPreferences: SharedPreferences) {

    fun cleanCache() {
        sharedPreferences.edit { clear() }
    }

    var defaultLanguage: String?
        get() = sharedPreferences.getString(CACHED_DEFAULT_LANGUAGE, null)
        set(language) = sharedPreferences.edit { putString(CACHED_DEFAULT_LANGUAGE, language) }

    companion object {
        const val CACHED_DEFAULT_LANGUAGE = "cached_locale"
    }

}