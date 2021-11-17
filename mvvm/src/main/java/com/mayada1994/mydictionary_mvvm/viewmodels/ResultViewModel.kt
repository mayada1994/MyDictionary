package com.mayada1994.mydictionary_mvvm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mayada1994.mydictionary_mvvm.di.DictionaryComponent
import com.mayada1994.mydictionary_mvvm.entities.LanguageInfo
import com.mayada1994.mydictionary_mvvm.utils.LanguageUtils
import com.mayada1994.mydictionary_mvvm.utils.SingleLiveEvent

class ResultViewModel : ViewModel() {

    private val _defaultLanguage = SingleLiveEvent<LanguageInfo>()
    val defaultLanguage: LiveData<LanguageInfo>
        get() = _defaultLanguage

    fun init() {
        DictionaryComponent.cacheUtils.defaultLanguage?.let {
            LanguageUtils.getLanguageByCode(it)?.let {
                _defaultLanguage.postValue(it)
            }
        }
    }

}