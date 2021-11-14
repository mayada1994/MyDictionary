package com.mayada1994.mydictionary_mvp.presenters

import com.mayada1994.mydictionary_mvp.fragments.MainFragment
import com.mayada1994.mydictionary_mvp.contracts.MainContract
import com.mayada1994.mydictionary_mvp.di.DictionaryComponent
import com.mayada1994.mydictionary_mvp.fragments.AddLanguagesFragment

class MainPresenter(
    private val viewInterface: MainContract.ViewInterface
) : MainContract.PresenterInterface {

    override fun init() {
        if (DictionaryComponent.cacheUtils.defaultLanguage.isNullOrBlank()) {
            viewInterface.setFragment(AddLanguagesFragment::class.java)
        } else {
            viewInterface.setFragment(MainFragment::class.java)
        }
    }

}