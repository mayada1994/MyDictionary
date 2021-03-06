package com.mayada1994.mydictionary_mvi.views

import com.mayada1994.mydictionary_mvi.entities.Word
import com.mayada1994.mydictionary_mvi.states.DictionaryState
import io.reactivex.Observable

interface DictionaryView {
    fun render(state: DictionaryState)
    fun displayWordsIntent(): Observable<Unit>
    fun addButtonClickIntent(): Observable<Unit>
    fun saveButtonClickIntent(): Observable<Pair<String?, String?>>
    fun deleteButtonClickIntent(): Observable<Word>
}