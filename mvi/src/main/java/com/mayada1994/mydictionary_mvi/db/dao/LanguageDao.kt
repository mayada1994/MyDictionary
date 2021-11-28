package com.mayada1994.mydictionary_mvi.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mayada1994.mydictionary_mvi.entities.Language
import io.reactivex.Single

@Dao
interface LanguageDao {

    @Query("SELECT * FROM languages")
    fun getLanguages(): Single<List<Language>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLanguages(languages: List<Language>): Single<Unit>

}