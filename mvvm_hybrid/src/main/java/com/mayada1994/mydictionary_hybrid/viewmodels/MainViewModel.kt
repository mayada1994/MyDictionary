package com.mayada1994.mydictionary_hybrid.viewmodels

import androidx.fragment.app.Fragment
import com.mayada1994.mydictionary_hybrid.di.DictionaryComponent
import com.mayada1994.mydictionary_hybrid.fragments.AddLanguagesFragment
import com.mayada1994.mydictionary_hybrid.fragments.MainFragment
import com.mayada1994.mydictionary_hybrid.utils.ViewEvent

class MainViewModel : BaseViewModel() {

    sealed class MainEvent {
        data class ShowSelectedScreen(
            val fragmentClass: Class<out Fragment>
        ) : ViewEvent
    }

    fun init() {
        setEvent(
            if (DictionaryComponent.cacheUtils.defaultLanguage.isNullOrBlank()) {
                MainEvent.ShowSelectedScreen(AddLanguagesFragment::class.java)
            } else {
                MainEvent.ShowSelectedScreen(MainFragment::class.java)
            }
        )
    }

}