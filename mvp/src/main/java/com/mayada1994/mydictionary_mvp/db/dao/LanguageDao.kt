package com.mayada1994.mydictionary_mvp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mayada1994.mydictionary_mvp.entities.Language
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface LanguageDao {

    @Query("SELECT * FROM languages")
    fun getLanguages(): Single<List<Language>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLanguages(languages: List<Language>): Completable

}