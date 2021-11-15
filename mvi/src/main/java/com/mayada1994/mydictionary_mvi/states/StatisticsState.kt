package com.mayada1994.mydictionary_mvi.states

import androidx.annotation.StringRes
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.entities.Statistics

sealed class StatisticsState {

    data class DataState(val defaultLanguage: LanguageInfo, val stats: List<Statistics>) : StatisticsState()

    object LoadingState: StatisticsState()

    data class EmptyState(val defaultLanguage: LanguageInfo) : StatisticsState()

    data class ErrorState(val defaultLanguage: LanguageInfo, @StringRes val resId: Int) : StatisticsState()

}
