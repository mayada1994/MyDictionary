package com.mayada1994.mydictionary_mvi.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mayada1994.mydictionary_mvi.entities.Language
import com.mayada1994.mydictionary_mvi.entities.Statistics
import com.mayada1994.mydictionary_mvi.entities.Word
import com.mayada1994.mydictionary_mvi.db.dao.LanguageDao
import com.mayada1994.mydictionary_mvi.db.dao.StatisticsDao
import com.mayada1994.mydictionary_mvi.db.dao.WordDao

@Database(
    entities = [Language::class, Statistics::class, Word::class],
    version = 1,
    exportSchema = false
)
abstract class DictionaryDatabase : RoomDatabase() {

    abstract fun languageDao(): LanguageDao

    abstract fun statisticsDao(): StatisticsDao

    abstract fun wordDao(): WordDao

}