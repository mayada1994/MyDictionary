package com.mayada1994.mydictionary_mvi.di

import android.app.Application
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.mayada1994.mydictionary_mvi.db.DictionaryDatabase
import com.mayada1994.mydictionary_mvi.db.dao.LanguageDao
import com.mayada1994.mydictionary_mvi.db.dao.StatisticsDao
import com.mayada1994.mydictionary_mvi.db.dao.WordDao
import com.mayada1994.mydictionary_mvi.repositories.LanguageRepository
import com.mayada1994.mydictionary_mvi.repositories.StatisticsRepository
import com.mayada1994.mydictionary_mvi.repositories.WordRepository
import com.mayada1994.mydictionary_mvi.utils.CacheUtils

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