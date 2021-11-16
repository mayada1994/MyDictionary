package com.mayada1994.mydictionary_mvvm.di

import android.app.Application
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.mayada1994.mydictionary_mvvm.repositories.LanguageRepository
import com.mayada1994.mydictionary_mvvm.repositories.StatisticsRepository
import com.mayada1994.mydictionary_mvvm.repositories.WordRepository
import com.mayada1994.mydictionary_mvvm.utils.CacheUtils
import com.mayada1994.mydictionary_mvvm.db.DictionaryDatabase
import com.mayada1994.mydictionary_mvvm.db.dao.LanguageDao
import com.mayada1994.mydictionary_mvvm.db.dao.StatisticsDao
import com.mayada1994.mydictionary_mvvm.db.dao.WordDao

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

    val languageRepository: LanguageRepository by lazy { LanguageRepository(languageDao) }

    val statisticsRepository: StatisticsRepository by lazy { StatisticsRepository(statisticsDao) }

    val wordRepository: WordRepository by lazy { WordRepository(wordDao) }

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