package com.mayada1994.mydictionary_mvi.interactors

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.repositories.StatisticsRepository
import com.mayada1994.mydictionary_mvi.states.StatisticsState
import com.mayada1994.mydictionary_mvi.utils.CacheUtils
import com.mayada1994.mydictionary_mvi.utils.LanguageUtils
import io.reactivex.Observable

class StatisticsInteractor(
    private val statisticsRepository: StatisticsRepository,
    private val cacheUtils: CacheUtils
) {

    private var defaultLanguage: String? = null

    fun getData(): Observable<StatisticsState> {
        cacheUtils.defaultLanguage?.let {
            defaultLanguage = it
            return getStats(LanguageUtils.getLanguageByCode(it)!!)
        }
        return Observable.just(StatisticsState.ErrorState(LanguageUtils.getLanguageByCode(defaultLanguage!!)!!, R.string.general_error))
    }

    private fun getStats(defaultLanguage: LanguageInfo): Observable<StatisticsState> {
            return statisticsRepository.getStatisticsByLanguage(defaultLanguage.locale)
                .map { stats ->
                    if (stats.isNotEmpty()) {
                        StatisticsState.DataState(defaultLanguage, stats)
                    } else {
                        StatisticsState.EmptyState(defaultLanguage)
                    }
                }
                .onErrorReturn { StatisticsState.ErrorState(defaultLanguage, R.string.general_error) }
                .toObservable()
    }

}