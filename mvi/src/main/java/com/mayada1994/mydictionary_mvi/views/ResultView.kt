package com.mayada1994.mydictionary_mvi.views

import com.mayada1994.mydictionary_mvi.states.ResultState
import io.reactivex.Observable

interface ResultView {
    fun render(state: ResultState)
    fun displayDataIntent(): Observable<Unit>
}