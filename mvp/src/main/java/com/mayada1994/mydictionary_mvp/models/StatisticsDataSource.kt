package com.mayada1994.mydictionary_mvp.models

import com.mayada1994.mydictionary_mvp.db.dao.StatisticsDao
import com.mayada1994.mydictionary_mvp.entities.Statistics
import io.reactivex.Completable
import io.reactivex.Single

class StatisticsDataSource(private val statisticsDao: StatisticsDao) {

    fun getStatisticsByLanguage(language: String): Single<List<Statistics>> = statisticsDao.getStatisticsByLanguage(language)

    fun insertStatistics(statistics: Statistics): Completable = statisticsDao.insertStatistics(statistics)

    fun deleteStatistics(statistics: Statistics): Completable = statisticsDao.deleteStatistics(statistics)

}