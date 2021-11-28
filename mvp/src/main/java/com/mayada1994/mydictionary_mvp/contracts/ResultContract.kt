package com.mayada1994.mydictionary_mvp.contracts

import com.mayada1994.mydictionary_mvp.entities.LanguageInfo

class ResultContract {

    interface PresenterInterface {
        fun init()
    }

    interface ViewInterface {
        fun setToolbar(defaultLanguage: LanguageInfo)
    }

}