package com.mayada1994.mydictionary.models

import com.mayada1994.mydictionary.db.dao.StatisticsDao
import com.mayada1994.mydictionary.entities.Statistics
import io.reactivex.Completable
import io.reactivex.Single

class StatisticsDataSource(private val statisticsDao: StatisticsDao) {

    fun getStatisticsByLanguage(language: String): Single<List<Statistics>> = statisticsDao.getStatisticsByLanguage(language)

    fun insertStatistics(statistics: Statistics): Completable = statisticsDao.insertStatistics(statistics)

    fun deleteStatistics(statistics: Statistics): Completable = statisticsDao.deleteStatistics(statistics)

}