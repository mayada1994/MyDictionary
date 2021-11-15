package com.mayada1994.mydictionary_mvi.views

import com.mayada1994.mydictionary_mvi.states.StatisticsState
import io.reactivex.Observable

interface StatisticsView {
    fun render(state: StatisticsState)
    fun displayStatisticsIntent(): Observable<Unit>
}