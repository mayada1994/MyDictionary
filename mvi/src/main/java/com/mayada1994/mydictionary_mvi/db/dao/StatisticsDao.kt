package com.mayada1994.mydictionary_mvi.db.dao

import androidx.room.*
import com.mayada1994.mydictionary_mvi.entities.Statistics
import io.reactivex.Single

@Dao
interface StatisticsDao {

    @Query("SELECT * FROM statistics WHERE language=:language")
    fun getStatisticsByLanguage(language: String): Single<List<Statistics>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStatistics(statistics: Statistics): Single<Unit>

    @Delete
    fun deleteStatistics(statistics: Statistics): Single<Unit>

}