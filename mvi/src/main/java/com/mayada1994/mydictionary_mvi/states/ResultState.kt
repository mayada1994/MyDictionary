package com.mayada1994.mydictionary_mvi.states

import androidx.annotation.StringRes
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo

sealed class ResultState {

    data class DataState(val defaultLanguage: LanguageInfo) : ResultState()

    data class ErrorState(@StringRes val resId: Int) : ResultState()

}
