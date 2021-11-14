package com.mayada1994.mydictionary_mvp.contracts

import androidx.annotation.StringRes
import com.mayada1994.mydictionary_mvp.entities.Language
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.items.DefaultLanguageItem

class DefaultLanguageContract {

    interface PresenterInterface {
        fun init()
        fun onAddButtonClick()
        fun setDefaultLanguage(language: DefaultLanguageItem)
        fun onDestroy()
    }

    interface ViewInterface {
        fun setToolbar(defaultLanguage: LanguageInfo)
        fun setLanguages(languages: List<DefaultLanguageItem>)
        fun changeAddButtonVisibility(isVisible: Boolean)
        fun navigateToAddLanguagesFragment(languages: List<Language>)
        fun showProgress(isVisible: Boolean)
        fun showMessage(@StringRes resId: Int)
    }

}