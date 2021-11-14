package com.mayada1994.mydictionary_mvi.repositories

import com.mayada1994.mydictionary_mvi.db.dao.StatisticsDao
import com.mayada1994.mydictionary_mvi.entities.Statistics
import io.reactivex.Single

class StatisticsRepository(private val statisticsDao: StatisticsDao) {

    fun getStatisticsByLanguage(language: String): Single<List<Statistics>> = statisticsDao.getStatisticsByLanguage(language)

    fun insertStatistics(statistics: Statistics): Single<Unit> = statisticsDao.insertStatistics(statistics)

    fun deleteStatistics(statistics: Statistics): Single<Unit> = statisticsDao.deleteStatistics(statistics)

}