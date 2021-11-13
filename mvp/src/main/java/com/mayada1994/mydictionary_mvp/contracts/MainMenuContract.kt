package com.mayada1994.mydictionary_mvp.contracts

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment

class MainMenuContract {

    interface PresenterInterface {
        fun onMenuItemSelected(@IdRes itemId: Int)
    }

    interface ViewInterface {
        fun showSelectedScreen(fragmentClass: Class<out Fragment>, selectedMenuItemId: Int)
    }

}