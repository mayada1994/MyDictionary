package com.mayada1994.mydictionary.items

import com.mayada1994.mydictionary.entities.Word

data class QuestionItem(
    val word: Word,
    val answers: List<String>,
    var selectedAnswer: String? = null
)