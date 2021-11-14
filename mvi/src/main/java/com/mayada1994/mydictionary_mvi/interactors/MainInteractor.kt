package com.mayada1994.mydictionary_mvi.interactors

import com.mayada1994.mydictionary_mvi.di.DictionaryComponent
import com.mayada1994.mydictionary_mvi.fragments.AddLanguagesFragment
import com.mayada1994.mydictionary_mvi.fragments.MainFragment
import com.mayada1994.mydictionary_mvi.states.MainState
import io.reactivex.Observable

class MainInteractor {

    fun getInitialScreen(): Observable<MainState> {
        return Observable.just(
            MainState.ScreenState(
                if (DictionaryComponent.cacheUtils.defaultLanguage.isNullOrBlank()) {
                    AddLanguagesFragment::class.java
                } else {
                    MainFragment::class.java
                }
            )
        )
    }

}