package com.mayada1994.mydictionary_mvi.repositories

import com.mayada1994.mydictionary_mvi.db.dao.WordDao
import com.mayada1994.mydictionary_mvi.entities.Word
import io.reactivex.Single

class WordRepository(private val wordDao: WordDao) {

    fun getWordsByLanguage(language: String): Single<List<Word>> = wordDao.getWordsByLanguage(language)

    fun insertWord(word: Word): Single<Unit> = wordDao.insertWord(word)

    fun deleteWord(word: Word): Single<Unit> = wordDao.deleteWord(word)

}