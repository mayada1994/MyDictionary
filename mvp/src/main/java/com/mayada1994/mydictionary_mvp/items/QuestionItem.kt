package com.mayada1994.mydictionary_mvp.items

import com.mayada1994.mydictionary_mvp.entities.Word

data class QuestionItem(
    val word: Word,
    val answers: List<String>,
    var selectedAnswer: String? = null
)