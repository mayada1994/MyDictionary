package com.mayada1994.mydictionary.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mayada1994.mydictionary.db.dao.LanguageDao
import com.mayada1994.mydictionary.db.dao.StatisticsDao
import com.mayada1994.mydictionary.db.dao.WordDao
import com.mayada1994.mydictionary.entities.Language
import com.mayada1994.mydictionary.entities.Statistics
import com.mayada1994.mydictionary.entities.Word

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