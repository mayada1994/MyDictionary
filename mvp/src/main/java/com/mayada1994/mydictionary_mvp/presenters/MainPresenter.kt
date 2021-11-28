package com.mayada1994.mydictionary_mvp.presenters

import com.mayada1994.mydictionary_mvp.contracts.MainContract
import com.mayada1994.mydictionary_mvp.fragments.AddLanguagesFragment
import com.mayada1994.mydictionary_mvp.fragments.MainFragment
import com.mayada1994.mydictionary_mvp.utils.CacheUtils

class MainPresenter(
    private val viewInterface: MainContract.ViewInterface,
    private val cacheUtils: CacheUtils
) : MainContract.PresenterInterface {

    override fun init() {
        if (cacheUtils.defaultLanguage.isNullOrBlank()) {
            viewInterface.setFragment(AddLanguagesFragment::class.java)
        } else {
            viewInterface.setFragment(MainFragment::class.java)
        }
    }

}