package com.mayada1994.mydictionary_hybrid.viewmodels

import com.mayada1994.mydictionary_hybrid.di.DictionaryComponent
import com.mayada1994.mydictionary_hybrid.utils.LanguageUtils

class ResultViewModel : BaseViewModel() {

    fun init() {
        DictionaryComponent.cacheUtils.defaultLanguage?.let {
            LanguageUtils.getLanguageByCode(it)?.let {
               setEvent(BaseEvent.SetDefaultLanguage(it))
            }
        }
    }

}