package com.mayada1994.mydictionary_mvc.items

import com.mayada1994.mydictionary_mvc.entities.Word

data class QuestionItem(
    val word: Word,
    val answers: List<String>,
    var selectedAnswer: String? = null
)