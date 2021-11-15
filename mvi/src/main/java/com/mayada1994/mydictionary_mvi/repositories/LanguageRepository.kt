package com.mayada1994.mydictionary_mvi.repositories

import com.mayada1994.mydictionary_mvi.db.dao.LanguageDao
import com.mayada1994.mydictionary_mvi.entities.Language
import io.reactivex.Single

class LanguageRepository(private val languageDao: LanguageDao) {

    fun getLanguages(): Single<List<Language>> = languageDao.getLanguages()

    fun insertLanguage(language: Language): Single<Unit> = languageDao.insertLanguage(language)

    fun insertLanguages(languages: List<Language>): Single<Unit> = languageDao.insertLanguages(languages)

    fun deleteLanguage(language: Language): Single<Unit> = languageDao.deleteLanguage(language)

}