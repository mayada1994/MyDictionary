package com.mayada1994.mydictionary.db.dao

import androidx.room.*
import com.mayada1994.mydictionary.entities.Statistics
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface StatisticsDao {

    @Query("SELECT * FROM words WHERE language=:language")
    fun getStatisticsByLanguage(language: Int): Single<List<Statistics>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStatistics(statistics: Statistics): Completable

    @Delete
    fun deleteStatistics(statistics: Statistics): Completable

}