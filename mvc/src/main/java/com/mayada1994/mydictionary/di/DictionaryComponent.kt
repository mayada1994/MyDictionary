package com.mayada1994.mydictionary.di

import android.app.Application

object DictionaryComponent {

    private lateinit var application: Application

    fun init(application: Application) {
        DictionaryComponent.application = application
    }

}