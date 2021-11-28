package com.mayada1994.mydictionary_mvp.contracts

import androidx.fragment.app.Fragment

class MainContract {

    interface PresenterInterface {
        fun init()
    }

    interface ViewInterface {
        fun setFragment(fragmentClass: Class<out Fragment>)
    }

}