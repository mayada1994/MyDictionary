package com.mayada1994.mydictionary_mvi.views

import com.mayada1994.mydictionary_mvi.items.QuestionItem
import com.mayada1994.mydictionary_mvi.states.QuizState
import io.reactivex.Observable

interface QuizView {
    fun render(state: QuizState)
    fun displayQuestionsIntent(): Observable<Unit>
    fun displayResultIntent(): Observable<List<QuestionItem>?>
}