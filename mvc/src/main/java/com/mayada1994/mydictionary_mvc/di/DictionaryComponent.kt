package com.mayada1994.mydictionary_mvc.di

import android.app.Application
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.mayada1994.mydictionary_mvc.db.DictionaryDatabase
import com.mayada1994.mydictionary_mvc.db.dao.LanguageDao
import com.mayada1994.mydictionary_mvc.db.dao.StatisticsDao
import com.mayada1994.mydictionary_mvc.db.dao.WordDao
import com.mayada1994.mydictionary_mvc.models.LanguageDataSource
import com.mayada1994.mydictionary_mvc.models.StatisticsDataSource
import com.mayada1994.mydictionary_mvc.models.WordDataSource
import com.mayada1994.mydictionary_mvc.utils.CacheUtils

object DictionaryComponent {

    private lateinit var application: Application

    private val database by lazy {
        Room.databaseBuilder(
            application.applicationContext,
            DictionaryDatabase::class.java, "dictionary.db"
        ).build()
    }

    private val languageDao: LanguageDao by lazy { database.languageDao() }

    private val statisticsDao: StatisticsDao by lazy { database.statisticsDao() }

    private val wordDao: WordDao by lazy { database.wordDao() }

    val languageDataSource: LanguageDataSource by lazy { LanguageDataSource(languageDao) }

    val statisticsDataSource: StatisticsDataSource by lazy { StatisticsDataSource(statisticsDao) }

    val wordDataSource: WordDataSource by lazy { WordDataSource(wordDao) }

    val cacheUtils: CacheUtils by lazy {
        CacheUtils(
            PreferenceManager.getDefaultSharedPreferences(
                application
            )
        )
    }

    fun init(application: Application) {
        DictionaryComponent.application = application
    }

}