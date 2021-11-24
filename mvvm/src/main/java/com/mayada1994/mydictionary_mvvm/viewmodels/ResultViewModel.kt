package com.mayada1994.mydictionary_mvvm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mayada1994.mydictionary_mvvm.entities.LanguageInfo
import com.mayada1994.mydictionary_mvvm.utils.CacheUtils
import com.mayada1994.mydictionary_mvvm.utils.LanguageUtils
import com.mayada1994.mydictionary_mvvm.utils.SingleLiveEvent

class ResultViewModel(private val cacheUtils: CacheUtils) : ViewModel() {

    private val _defaultLanguage = SingleLiveEvent<LanguageInfo>()
    val defaultLanguage: LiveData<LanguageInfo>
        get() = _defaultLanguage

    fun init() {
        cacheUtils.defaultLanguage?.let {
            LanguageUtils.getLanguageByCode(it)?.let {
                _defaultLanguage.postValue(it)
            }
        }
    }

}