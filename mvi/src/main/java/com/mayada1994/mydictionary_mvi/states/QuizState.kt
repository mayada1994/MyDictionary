package com.mayada1994.mydictionary_mvi.states

import androidx.annotation.StringRes
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.items.QuestionItem

sealed class QuizState {

    data class DataState(val defaultLanguage: LanguageInfo, val questions: List<QuestionItem>) : QuizState()

    data class ResultState(val result: String): QuizState()

    object LoadingState : QuizState()

    data class EmptyState(val defaultLanguage: LanguageInfo) : QuizState()

    data class CompletedState(@StringRes val resId: Int) : QuizState()

    data class ErrorState(@StringRes val resId: Int) : QuizState()
    
}