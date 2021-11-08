package com.mayada1994.mydictionary.models

import com.mayada1994.mydictionary.db.dao.WordDao
import com.mayada1994.mydictionary.entities.Word
import io.reactivex.Completable
import io.reactivex.Single

class WordDataSource(private val wordDao: WordDao) {

    fun getWordsByLanguage(language: String): Single<List<Word>> = wordDao.getWordsByLanguage(language)

    fun insertWord(word: Word): Completable = wordDao.insertWord(word)

    fun deleteWord(word: Word): Completable = wordDao.deleteWord(word)

}