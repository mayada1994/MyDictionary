package com.mayada1994.mydictionary_hybrid.di

import android.app.Application
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.mayada1994.mydictionary_hybrid.db.DictionaryDatabase
import com.mayada1994.mydictionary_hybrid.db.dao.LanguageDao
import com.mayada1994.mydictionary_hybrid.db.dao.StatisticsDao
import com.mayada1994.mydictionary_hybrid.db.dao.WordDao
import com.mayada1994.mydictionary_hybrid.repositories.LanguageRepository
import com.mayada1994.mydictionary_hybrid.repositories.StatisticsRepository
import com.mayada1994.mydictionary_hybrid.repositories.WordRepository
import com.mayada1994.mydictionary_hybrid.utils.CacheUtils
import com.mayada1994.mydictionary_hybrid.viewmodels.ViewModelFactory

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

    private val languageRepository: LanguageRepository by lazy { LanguageRepository(languageDao) }

    private val statisticsRepository: StatisticsRepository by lazy { StatisticsRepository(
        statisticsDao
    ) }

    private val wordRepository: WordRepository by lazy { WordRepository(wordDao) }

    private val cacheUtils: CacheUtils by lazy {
        CacheUtils(
            PreferenceManager.getDefaultSharedPreferences(
                application
            )
        )
    }

    val viewModelFactory: ViewModelFactory by lazy { ViewModelFactory(languageRepository, statisticsRepository, wordRepository, cacheUtils) }

    fun init(application: Application) {
        DictionaryComponent.application = application
    }

}