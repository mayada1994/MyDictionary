package com.mayada1994.mydictionary_hybrid.repositories

import com.mayada1994.mydictionary_hybrid.db.dao.LanguageDao
import com.mayada1994.mydictionary_hybrid.entities.Language
import io.reactivex.Completable
import io.reactivex.Single

class LanguageRepository(private val languageDao: LanguageDao) {

    fun getLanguages(): Single<List<Language>> = languageDao.getLanguages()

    fun insertLanguages(languages: List<Language>): Completable = languageDao.insertLanguages(languages)

}