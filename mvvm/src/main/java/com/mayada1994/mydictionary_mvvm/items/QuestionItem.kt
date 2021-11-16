package com.mayada1994.mydictionary_mvvm.items

import com.mayada1994.mydictionary_mvvm.entities.Word

data class QuestionItem(
    val word: Word,
    val answers: List<String>,
    var selectedAnswer: String? = null
)