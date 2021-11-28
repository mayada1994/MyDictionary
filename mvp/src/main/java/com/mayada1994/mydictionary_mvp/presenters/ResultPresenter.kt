package com.mayada1994.mydictionary_mvp.presenters

import com.mayada1994.mydictionary_mvp.contracts.ResultContract
import com.mayada1994.mydictionary_mvp.utils.CacheUtils
import com.mayada1994.mydictionary_mvp.utils.LanguageUtils

class ResultPresenter(
    private val viewInterface: ResultContract.ViewInterface,
    private val cacheUtils: CacheUtils
): ResultContract.PresenterInterface {

    override fun init() {
        cacheUtils.defaultLanguage?.let {
            LanguageUtils.getLanguageByCode(it)?.let {
                viewInterface.setToolbar(it)
            }
        }
    }

}