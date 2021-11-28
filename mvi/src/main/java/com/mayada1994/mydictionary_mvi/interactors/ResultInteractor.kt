package com.mayada1994.mydictionary_mvi.interactors

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.states.ResultState
import com.mayada1994.mydictionary_mvi.utils.CacheUtils
import com.mayada1994.mydictionary_mvi.utils.LanguageUtils
import io.reactivex.Observable

class ResultInteractor(private val cacheUtils: CacheUtils) {

    fun getData(): Observable<ResultState> {
        cacheUtils.defaultLanguage?.let {
            LanguageUtils.getLanguageByCode(it)?.let {
                return Observable.just(ResultState.DataState(it))
            }
        }
        return Observable.just(ResultState.ErrorState(R.string.general_error))
    }

}