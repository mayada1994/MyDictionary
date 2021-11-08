package com.mayada1994.mydictionary.db.dao

import androidx.room.*
import com.mayada1994.mydictionary.entities.Word
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface WordDao {

    @Query("SELECT * FROM words WHERE language=:language")
    fun getWordsByLanguage(language: String): Single<List<Word>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWord(word: Word): Completable

    @Delete
    fun deleteWord(word: Word): Completable

}