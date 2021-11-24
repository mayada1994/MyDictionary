package com.mayada1994.mydictionary_hybrid.viewmodels

import com.mayada1994.mydictionary_hybrid.events.MainEvent
import com.mayada1994.mydictionary_hybrid.fragments.AddLanguagesFragment
import com.mayada1994.mydictionary_hybrid.fragments.MainFragment
import com.mayada1994.mydictionary_hybrid.utils.CacheUtils

class MainViewModel(private val cacheUtils: CacheUtils) : BaseViewModel() {

    fun init() {
        setEvent(
            if (cacheUtils.defaultLanguage.isNullOrBlank()) {
                MainEvent.ShowSelectedScreen(AddLanguagesFragment::class.java)
            } else {
                MainEvent.ShowSelectedScreen(MainFragment::class.java)
            }
        )
    }

}