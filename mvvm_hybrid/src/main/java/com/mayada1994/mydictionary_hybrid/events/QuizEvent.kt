package com.mayada1994.mydictionary_hybrid.events

import com.mayada1994.mydictionary_hybrid.items.QuestionItem

sealed class QuizEvent {
    data class SetQuestions(val questions: List<QuestionItem>) : ViewEvent

    data class SetResult(val result: String) : ViewEvent

    data class SetResultButtonVisibility(val isVisible: Boolean) : ViewEvent
}