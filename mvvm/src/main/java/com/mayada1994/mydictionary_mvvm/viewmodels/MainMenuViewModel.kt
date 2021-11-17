package com.mayada1994.mydictionary_mvvm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mayada1994.mydictionary_mvvm.R
import com.mayada1994.mydictionary_mvvm.entities.SelectedScreen
import com.mayada1994.mydictionary_mvvm.fragments.DefaultLanguageFragment
import com.mayada1994.mydictionary_mvvm.fragments.DictionaryFragment
import com.mayada1994.mydictionary_mvvm.fragments.QuizFragment
import com.mayada1994.mydictionary_mvvm.fragments.StatisticsFragment
import com.mayada1994.mydictionary_mvvm.utils.SingleLiveEvent

class MainMenuViewModel : ViewModel() {

    private val _selectedScreen = SingleLiveEvent<SelectedScreen>()
    val selectedScreen: LiveData<SelectedScreen>
        get() = _selectedScreen

    private val _toastMessageStringResId = SingleLiveEvent<Int>()
    val toastMessageStringResId: LiveData<Int>
        get() = _toastMessageStringResId

    fun onMenuItemSelected(itemId: Int) {
        when (itemId) {
            R.id.dictionary_menu_item -> SelectedScreen(
                DictionaryFragment::class.java,
                selectedMenuItemId = 0
            )

            R.id.quiz_menu_item -> SelectedScreen(
                QuizFragment::class.java,
                selectedMenuItemId = 1
            )

            R.id.languages_menu_item -> SelectedScreen(
                DefaultLanguageFragment::class.java,
                selectedMenuItemId = 2
            )

            R.id.statistics_menu_item -> SelectedScreen(
                StatisticsFragment::class.java,
                selectedMenuItemId = 3
            )

            else -> _toastMessageStringResId.postValue(R.string.general_error)
        }
    }

}