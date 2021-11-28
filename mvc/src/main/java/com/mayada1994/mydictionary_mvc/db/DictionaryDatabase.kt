package com.mayada1994.mydictionary_mvc.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mayada1994.mydictionary_mvc.db.dao.LanguageDao
import com.mayada1994.mydictionary_mvc.db.dao.StatisticsDao
import com.mayada1994.mydictionary_mvc.db.dao.WordDao
import com.mayada1994.mydictionary_mvc.entities.Language
import com.mayada1994.mydictionary_mvc.entities.Statistics
import com.mayada1994.mydictionary_mvc.entities.Word

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