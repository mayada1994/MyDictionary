package com.mayada1994.mydictionary_mvvm.db.dao

import androidx.room.*
import com.mayada1994.mydictionary_mvvm.entities.Word
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface WordDao {

    @Query("SELECT * FROM words WHERE language=:language ORDER BY name")
    fun getWordsByLanguage(language: String): Single<List<Word>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWord(word: Word): Completable

    @Delete
    fun deleteWord(word: Word): Completable

}