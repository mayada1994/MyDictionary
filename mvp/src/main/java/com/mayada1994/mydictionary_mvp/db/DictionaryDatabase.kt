package com.mayada1994.mydictionary_mvp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mayada1994.mydictionary_mvp.db.dao.LanguageDao
import com.mayada1994.mydictionary_mvp.db.dao.StatisticsDao
import com.mayada1994.mydictionary_mvp.db.dao.WordDao
import com.mayada1994.mydictionary_mvp.entities.Language
import com.mayada1994.mydictionary_mvp.entities.Statistics
import com.mayada1994.mydictionary_mvp.entities.Word

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