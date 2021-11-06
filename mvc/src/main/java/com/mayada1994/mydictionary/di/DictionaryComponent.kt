package com.mayada1994.mydictionary.di

import android.app.Application
import androidx.room.Room
import com.mayada1994.mydictionary.db.DictionaryDatabase

object DictionaryComponent {

    private lateinit var application: Application

    private val database by lazy {
        Room.databaseBuilder(
            application.applicationContext,
            DictionaryDatabase::class.java, "dictionary.db"
        ).build()
    }

    fun init(application: Application) {
        DictionaryComponent.application = application
    }

}