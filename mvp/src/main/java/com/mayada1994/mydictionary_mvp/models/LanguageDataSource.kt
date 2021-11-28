package com.mayada1994.mydictionary_mvp.models

import com.mayada1994.mydictionary_mvp.db.dao.LanguageDao
import com.mayada1994.mydictionary_mvp.entities.Language
import io.reactivex.Completable
import io.reactivex.Single

class LanguageDataSource(private val languageDao: LanguageDao) {

    fun getLanguages(): Single<List<Language>> = languageDao.getLanguages()

    fun insertLanguages(languages: List<Language>): Completable = languageDao.insertLanguages(languages)

}