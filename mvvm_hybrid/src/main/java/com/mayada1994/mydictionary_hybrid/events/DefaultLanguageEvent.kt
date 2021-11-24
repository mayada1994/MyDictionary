package com.mayada1994.mydictionary_hybrid.events

import com.mayada1994.mydictionary_hybrid.entities.Language
import com.mayada1994.mydictionary_hybrid.items.DefaultLanguageItem

sealed class DefaultLanguageEvent {
    data class SetLanguages(val languages: List<DefaultLanguageItem>) : ViewEvent

    data class SetAddButtonVisibility(val isVisible: Boolean) : ViewEvent

    data class NavigateToAddLanguagesFragment(val languages: List<Language>) : ViewEvent
}