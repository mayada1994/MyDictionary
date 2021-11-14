package com.mayada1994.mydictionary_mvp.contracts

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.mayada1994.mydictionary_mvp.entities.Language
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.items.LanguageItem

class AddLanguagesContract {

    interface PresenterInterface {
        fun init(usedLanguages: List<Language>)
        fun onSaveButtonClick()
        fun saveLanguages(languages: List<LanguageInfo>)
        fun onLanguagesSelected(selectedLanguages: List<LanguageInfo>)
        fun onDestroy()
    }

    interface ViewInterface {
        fun setLanguages(languages: List<LanguageItem>)
        fun setFragment(fragmentClass: Class<out Fragment>)
        fun onBackPressed()
        fun showProgress(isVisible: Boolean)
        fun showMessage(@StringRes resId: Int)
    }

}