package com.mayada1994.mydictionary_mvi.states

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.mayada1994.mydictionary_mvi.items.LanguageItem

sealed class AddLanguagesState {

    data class DataState(val languages: List<LanguageItem>): AddLanguagesState()

    data class ScreenState(val fragmentClass: Class<out Fragment>): AddLanguagesState()

    object BackPressedState: AddLanguagesState()

    object LoadingState : AddLanguagesState()

    data class CompletedState(@StringRes val resId: Int) : AddLanguagesState()

}