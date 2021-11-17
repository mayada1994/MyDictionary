package com.mayada1994.mydictionary_mvvm.repositories

import com.mayada1994.mydictionary_mvvm.db.dao.LanguageDao
import com.mayada1994.mydictionary_mvvm.entities.Language
import io.reactivex.Completable
import io.reactivex.Single

class LanguageRepository(private val languageDao: LanguageDao) {

    fun getLanguages(): Single<List<Language>> = languageDao.getLanguages()

    fun insertLanguage(language: Language): Completable = languageDao.insertLanguage(language)

    fun insertLanguages(languages: List<Language>): Completable = languageDao.insertLanguages(languages)

    fun deleteLanguage(language: Language): Completable = languageDao.deleteLanguage(language)

}