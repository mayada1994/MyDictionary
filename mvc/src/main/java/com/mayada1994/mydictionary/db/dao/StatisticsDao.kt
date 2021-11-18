package com.mayada1994.mydictionary.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mayada1994.mydictionary.entities.Statistics
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface StatisticsDao {

    @Query("SELECT * FROM statistics WHERE language=:language")
    fun getStatisticsByLanguage(language: String): Single<List<Statistics>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStatistics(statistics: Statistics): Completable

}