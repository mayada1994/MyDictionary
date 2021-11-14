package com.mayada1994.mydictionary_mvi.views

import com.mayada1994.mydictionary_mvi.states.MainMenuState
import io.reactivex.Observable

interface MainMenuView {

    fun render(state: MainMenuState)
    fun selectMenuItemIntent(): Observable<Int>

}