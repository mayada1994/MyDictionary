package com.mayada1994.mydictionary_mvvm.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mayada1994.mydictionary_mvvm.fragments.AddLanguagesFragment
import com.mayada1994.mydictionary_mvvm.fragments.MainFragment
import com.mayada1994.mydictionary_mvvm.utils.CacheUtils
import com.mayada1994.mydictionary_mvvm.utils.SingleLiveEvent

class MainViewModel(private val cacheUtils: CacheUtils) : ViewModel() {

    private val _selectedScreen = SingleLiveEvent<Class<out Fragment>>()
    val selectedScreen: LiveData<Class<out Fragment>>
        get() = _selectedScreen

    fun init() {
        if (cacheUtils.defaultLanguage.isNullOrBlank()) {
            _selectedScreen.postValue(AddLanguagesFragment::class.java)
        } else {
            _selectedScreen.postValue(MainFragment::class.java)
        }
    }

}