package com.mayada1994.mydictionary_hybrid.items

import com.mayada1994.mydictionary_hybrid.entities.Word

data class QuestionItem(
    val word: Word,
    val answers: List<String>,
    var selectedAnswer: String? = null
)