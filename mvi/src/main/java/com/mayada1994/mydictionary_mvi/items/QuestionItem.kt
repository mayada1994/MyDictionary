package com.mayada1994.mydictionary_mvi.items

import com.mayada1994.mydictionary_mvi.entities.Word

data class QuestionItem(
    val word: Word,
    val answers: List<String>,
    var selectedAnswer: String? = null
)