package com.mayada1994.mydictionary.models

import com.mayada1994.mydictionary.db.dao.LanguageDao
import com.mayada1994.mydictionary.entities.Language
import io.reactivex.Completable
import io.reactivex.Single

class LanguageDataSource(private val languageDao: LanguageDao) {

    fun getLanguages(): Single<List<Language>> = languageDao.getLanguages()

    fun insertLanguage(language: Language): Completable = languageDao.insertLanguage(language)

    fun deleteLanguage(language: Language): Completable = languageDao.deleteLanguage(language)

}