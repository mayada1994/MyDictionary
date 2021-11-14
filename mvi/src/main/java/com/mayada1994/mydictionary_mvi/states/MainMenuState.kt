package com.mayada1994.mydictionary_mvi.states

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

sealed class MainMenuState {

    data class ScreenState(
        val fragmentClass: Class<out Fragment>,
        val selectedMenuItemId: Int
    ) : MainMenuState()

    data class ErrorState(@StringRes val resId: Int) : MainMenuState()

}