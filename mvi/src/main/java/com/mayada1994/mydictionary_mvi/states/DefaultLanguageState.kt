package com.mayada1994.mydictionary_mvi.states

import androidx.annotation.StringRes
import com.mayada1994.mydictionary_mvi.entities.Language
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.items.DefaultLanguageItem

sealed class DefaultLanguageState {

    data class DataState(val defaultLanguage: LanguageInfo, val languages: List<DefaultLanguageItem>, val isVisible: Boolean) : DefaultLanguageState()

    data class ToolbarState(val defaultLanguage: LanguageInfo): DefaultLanguageState()

    data class NavigateToAddLanguagesFragmentState(val languages: List<Language>): DefaultLanguageState()

    object LoadingState : DefaultLanguageState()

    data class ErrorState(val defaultLanguage: LanguageInfo, @StringRes val resId: Int) : DefaultLanguageState()

}