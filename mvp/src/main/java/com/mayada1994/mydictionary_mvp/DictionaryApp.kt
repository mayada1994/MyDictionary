package com.mayada1994.mydictionary_mvp

import android.app.Application
import com.mayada1994.mydictionary_mvp.di.DictionaryComponent
import timber.log.Timber

class DictionaryApp : Application() {

    override fun onCreate() {
        super.onCreate()

        DictionaryComponent.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}