package com.mayada1994.mydictionary_mvi.views

import com.mayada1994.mydictionary_mvi.states.MainState
import io.reactivex.Observable

interface MainView {
    fun render(state: MainState)
    fun displayInitialScreenIntent(): Observable<Unit>
}