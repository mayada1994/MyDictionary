package com.mayada1994.mydictionary_mvp.di

import android.app.Application
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.mayada1994.mydictionary_mvp.models.LanguageDataSource
import com.mayada1994.mydictionary_mvp.models.StatisticsDataSource
import com.mayada1994.mydictionary_mvp.models.WordDataSource
import com.mayada1994.mydictionary_mvp.utils.CacheUtils
import com.mayada1994.mydictionary_mvp.db.DictionaryDatabase
import com.mayada1994.mydictionary_mvp.db.dao.LanguageDao
import com.mayada1994.mydictionary_mvp.db.dao.StatisticsDao
import com.mayada1994.mydictionary_mvp.db.dao.WordDao

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