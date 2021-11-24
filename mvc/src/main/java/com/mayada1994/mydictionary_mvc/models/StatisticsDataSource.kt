package com.mayada1994.mydictionary_mvc.models

import com.mayada1994.mydictionary_mvc.db.dao.StatisticsDao
import com.mayada1994.mydictionary_mvc.entities.Statistics
import io.reactivex.Completable
import io.reactivex.Single

class StatisticsDataSource(private val statisticsDao: StatisticsDao) {

    fun getStatisticsByLanguage(language: String): Single<List<Statistics>> = statisticsDao.getStatisticsByLanguage(language)

    fun insertStatistics(statistics: Statistics): Completable = statisticsDao.insertStatistics(statistics)

}