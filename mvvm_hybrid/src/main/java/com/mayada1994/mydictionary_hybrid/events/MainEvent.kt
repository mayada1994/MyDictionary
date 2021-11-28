package com.mayada1994.mydictionary_hybrid.events

import androidx.fragment.app.Fragment

sealed class MainEvent {
    data class ShowSelectedScreen(
        val fragmentClass: Class<out Fragment>
    ) : ViewEvent
}