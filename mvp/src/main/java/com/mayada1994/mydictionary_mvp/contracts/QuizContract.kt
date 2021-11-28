package com.mayada1994.mydictionary_mvp.contracts

import androidx.annotation.StringRes
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.items.QuestionItem

class QuizContract {

    interface PresenterInterface {
        fun init()
        fun getResult(questions: List<QuestionItem>?)
        fun onDestroy()
    }

    interface ViewInterface {
        fun setToolbar(defaultLanguage: LanguageInfo)
        fun setQuestions(questions: List<QuestionItem>)
        fun showResultFragment(result: String)
        fun showProgress(isVisible: Boolean)
        fun showPlaceholder(isVisible: Boolean)
        fun showMessage(@StringRes resId: Int)
        fun changeResultButtonVisibility(isVisible: Boolean)
    }

}