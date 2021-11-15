package com.mayada1994.mydictionary_mvi.views

import com.mayada1994.mydictionary_mvi.items.DefaultLanguageItem
import com.mayada1994.mydictionary_mvi.states.DefaultLanguageState
import io.reactivex.Observable

interface DefaultLanguageView {
    fun render(state: DefaultLanguageState)
    fun displayLanguagesIntent(): Observable<Unit>
    fun addButtonClickIntent(): Observable<Unit>
    fun setDefaultLanguageIntent(): Observable<DefaultLanguageItem>
}