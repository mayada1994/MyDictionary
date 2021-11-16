package com.mayada1994.mydictionary_mvvm.viewmodels

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mayada1994.mydictionary_mvvm.di.DictionaryComponent
import com.mayada1994.mydictionary_mvvm.utils.SingleLiveEvent

class MainViewModel : ViewModel() {

    private val _selectedScreen = SingleLiveEvent<Class<out Fragment>>()
    val selectedScreen: LiveData<Class<out Fragment>>
        get() = _selectedScreen

    fun init() {
        if (DictionaryComponent.cacheUtils.defaultLanguage.isNullOrBlank()) {
//            _selectedScreen.postValue(AddLanguagesFragment::class.java)
        } else {
//            _selectedScreen.postValue(MainFragment::class.java)
        }
    }

}