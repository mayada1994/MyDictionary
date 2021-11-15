package com.mayada1994.mydictionary_mvi.states

import androidx.fragment.app.Fragment

sealed class MainState {
    data class ScreenState(val fragmentClass: Class<out Fragment>) : MainState()
}