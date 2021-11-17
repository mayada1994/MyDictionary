package com.mayada1994.mydictionary_hybrid.db.dao

import androidx.room.*
import com.mayada1994.mydictionary_hybrid.entities.Statistics
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface StatisticsDao {

    @Query("SELECT * FROM statistics WHERE language=:language")
    fun getStatisticsByLanguage(language: String): Single<List<Statistics>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStatistics(statistics: Statistics): Completable

    @Delete
    fun deleteStatistics(statistics: Statistics): Completable

}