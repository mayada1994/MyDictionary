package com.mayada1994.mydictionary_mvi.interactors

import com.mayada1994.mydictionary_mvi.fragments.AddLanguagesFragment
import com.mayada1994.mydictionary_mvi.fragments.MainFragment
import com.mayada1994.mydictionary_mvi.states.MainState
import com.mayada1994.mydictionary_mvi.utils.CacheUtils
import io.reactivex.Observable

class MainInteractor(private val cacheUtils: CacheUtils) {

    fun getInitialScreen(): Observable<MainState> {
        return Observable.just(
            MainState.ScreenState(
                if (cacheUtils.defaultLanguage.isNullOrBlank()) {
                    AddLanguagesFragment::class.java
                } else {
                    MainFragment::class.java
                }
            )
        )
    }

}