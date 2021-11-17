package com.mayada1994.mydictionary_hybrid.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mayada1994.mydictionary_hybrid.db.dao.LanguageDao
import com.mayada1994.mydictionary_hybrid.db.dao.StatisticsDao
import com.mayada1994.mydictionary_hybrid.db.dao.WordDao
import com.mayada1994.mydictionary_hybrid.entities.Language
import com.mayada1994.mydictionary_hybrid.entities.Statistics
import com.mayada1994.mydictionary_hybrid.entities.Word

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