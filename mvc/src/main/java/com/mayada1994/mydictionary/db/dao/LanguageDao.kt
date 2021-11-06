package com.mayada1994.mydictionary.db.dao

import androidx.room.*
import com.mayada1994.mydictionary.entities.Language
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface LanguageDao {

    @Query("SELECT * FROM languages")
    fun getLanguages(): Single<List<Language>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLanguage(language: Language): Completable

    @Delete
    fun deleteLanguage(language: Language): Completable

}