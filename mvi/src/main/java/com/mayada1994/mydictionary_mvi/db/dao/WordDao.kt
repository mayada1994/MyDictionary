package com.mayada1994.mydictionary_mvi.db.dao

import androidx.room.*
import com.mayada1994.mydictionary_mvi.entities.Word
import io.reactivex.Single

@Dao
interface WordDao {

    @Query("SELECT * FROM words WHERE language=:language ORDER BY name")
    fun getWordsByLanguage(language: String): Single<List<Word>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWord(word: Word): Single<Unit>

    @Delete
    fun deleteWord(word: Word): Single<Unit>

}