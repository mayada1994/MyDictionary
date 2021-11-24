package com.mayada1994.mydictionary_hybrid.events

import androidx.annotation.StringRes
import com.mayada1994.mydictionary_hybrid.entities.LanguageInfo

sealed class BaseEvent {

    data class SetDefaultLanguage(val defaultLanguage: LanguageInfo) : ViewEvent

    data class ShowProgress(val isProgressVisible: Boolean): ViewEvent

    data class ShowPlaceholder(val isVisible: Boolean): ViewEvent

    data class ShowMessage(@StringRes val resId: Int): ViewEvent

}