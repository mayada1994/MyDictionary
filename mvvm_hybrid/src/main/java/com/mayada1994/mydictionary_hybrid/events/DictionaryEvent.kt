package com.mayada1994.mydictionary_hybrid.events

import com.mayada1994.mydictionary_hybrid.entities.Word

sealed class DictionaryEvent {
    data class SetWords(val words: List<Word>) : ViewEvent

    object ShowAddNewWordDialog : ViewEvent
}