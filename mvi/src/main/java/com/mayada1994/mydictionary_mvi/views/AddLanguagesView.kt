package com.mayada1994.mydictionary_mvi.views

import com.mayada1994.mydictionary_mvi.entities.Language
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.states.AddLanguagesState
import io.reactivex.Observable

interface AddLanguagesView {
    fun render(state: AddLanguagesState)
    fun displayLanguagesIntent(): Observable<List<Language>>
    fun saveButtonClickIntent(): Observable<Unit>
    fun selectLanguagesIntent(): Observable<List<LanguageInfo>>
}