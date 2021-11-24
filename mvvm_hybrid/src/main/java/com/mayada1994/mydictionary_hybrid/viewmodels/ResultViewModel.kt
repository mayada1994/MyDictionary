package com.mayada1994.mydictionary_hybrid.viewmodels

import com.mayada1994.mydictionary_hybrid.events.BaseEvent
import com.mayada1994.mydictionary_hybrid.utils.CacheUtils
import com.mayada1994.mydictionary_hybrid.utils.LanguageUtils

class ResultViewModel(private val cacheUtils: CacheUtils) : BaseViewModel() {

    fun init() {
        cacheUtils.defaultLanguage?.let {
            LanguageUtils.getLanguageByCode(it)?.let {
               setEvent(BaseEvent.SetDefaultLanguage(it))
            }
        }
    }

}