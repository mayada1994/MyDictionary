package com.mayada1994.mydictionary_hybrid.repositories

import com.mayada1994.mydictionary_hybrid.db.dao.StatisticsDao
import com.mayada1994.mydictionary_hybrid.entities.Statistics
import io.reactivex.Completable
import io.reactivex.Single

class StatisticsRepository(private val statisticsDao: StatisticsDao) {

    fun getStatisticsByLanguage(language: String): Single<List<Statistics>> = statisticsDao.getStatisticsByLanguage(language)

    fun insertStatistics(statistics: Statistics): Completable = statisticsDao.insertStatistics(statistics)

}