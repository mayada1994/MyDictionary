package com.mayada1994.mydictionary_mvp.contracts

import android.text.Editable
import androidx.annotation.StringRes
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.entities.Word

class DictionaryContract {

    interface PresenterInterface {
        fun init()
        fun onAddButtonClick()
        fun onSaveButtonClick(word: Editable?, translation: Editable?)
        fun onDeleteButtonClick(word: Word)
        fun onDestroy()
    }

    interface ViewInterface {
        fun setToolbar(defaultLanguage: LanguageInfo)
        fun setWords(words: List<Word>)
        fun showAddNewWordDialog()
        fun showProgress(isVisible: Boolean)
        fun showPlaceholder(isVisible: Boolean)
        fun showMessage(@StringRes resId: Int)
    }

}