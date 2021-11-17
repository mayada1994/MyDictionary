package com.mayada1994.mydictionary_mvvm.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mayada1994.mydictionary_mvvm.entities.Language
import com.mayada1994.mydictionary_mvvm.entities.Statistics
import com.mayada1994.mydictionary_mvvm.entities.Word
import com.mayada1994.mydictionary_mvvm.db.dao.LanguageDao
import com.mayada1994.mydictionary_mvvm.db.dao.StatisticsDao
import com.mayada1994.mydictionary_mvvm.db.dao.WordDao

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