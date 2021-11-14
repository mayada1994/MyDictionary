package com.mayada1994.mydictionary_mvi.db.dao

import androidx.room.*
import com.mayada1994.mydictionary_mvi.entities.Language
import io.reactivex.Single

@Dao
interface LanguageDao {

    @Query("SELECT * FROM languages")
    fun getLanguages(): Single<List<Language>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLanguage(language: Language): Single<Unit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLanguages(languages: List<Language>): Single<Unit>

    @Delete
    fun deleteLanguage(language: Language): Single<Unit>

}