package com.mayada1994.mydictionary_mvp.contracts

import androidx.annotation.StringRes
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.entities.Statistics

class StatisticsContract {

    interface PresenterInterface {
        fun init()
        fun onDestroy()
    }

    interface ViewInterface {
        fun setToolbar(defaultLanguage: LanguageInfo)
        fun setStats(stats: List<Statistics>)
        fun showProgress(isVisible: Boolean)
        fun showPlaceholder(isVisible: Boolean)
        fun showMessage(@StringRes resId: Int)
    }

}