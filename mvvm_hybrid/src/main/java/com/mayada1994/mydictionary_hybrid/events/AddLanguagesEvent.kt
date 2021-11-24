package com.mayada1994.mydictionary_hybrid.events

import androidx.fragment.app.Fragment
import com.mayada1994.mydictionary_hybrid.items.LanguageItem

sealed class AddLanguagesEvent {
    data class SetLanguages(val languages: List<LanguageItem>) : ViewEvent

    data class ShowSelectedScreen(val fragmentClass: Class<out Fragment>) : ViewEvent

    object OnBackPressed: ViewEvent
}