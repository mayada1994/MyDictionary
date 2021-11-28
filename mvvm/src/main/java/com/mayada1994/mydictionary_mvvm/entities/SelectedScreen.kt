package com.mayada1994.mydictionary_mvvm.entities

import androidx.fragment.app.Fragment

data class SelectedScreen(
    val fragmentClass: Class<out Fragment>,
    val selectedMenuItemId: Int
)