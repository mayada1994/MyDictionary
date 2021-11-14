package com.mayada1994.mydictionary_mvp.presenters

import com.mayada1994.mydictionary_mvp.contracts.ResultContract
import com.mayada1994.mydictionary_mvp.di.DictionaryComponent
import com.mayada1994.mydictionary_mvp.utils.LanguageUtils

class ResultPresenter(
    private val viewInterface: ResultContract.ViewInterface
): ResultContract.PresenterInterface {

    override fun init() {
        DictionaryComponent.cacheUtils.defaultLanguage?.let {
            LanguageUtils.getLanguageByCode(it)?.let {
                viewInterface.setToolbar(it)
            }
        }
    }

}