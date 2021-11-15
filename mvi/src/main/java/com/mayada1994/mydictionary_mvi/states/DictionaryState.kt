package com.mayada1994.mydictionary_mvi.states

import androidx.annotation.StringRes
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.entities.Word

sealed class DictionaryState {

    data class DataState(val defaultLanguage: LanguageInfo, val words: List<Word>, @StringRes val resId: Int? = null) : DictionaryState()

    object ShowAddNewWordDialogState : DictionaryState()

    object LoadingState : DictionaryState()

    data class EmptyState(val defaultLanguage: LanguageInfo) : DictionaryState()

    data class CompletedState(@StringRes val resId: Int) : DictionaryState()

    data class ErrorState(@StringRes val resId: Int) : DictionaryState()

}
