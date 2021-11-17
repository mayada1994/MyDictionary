package com.mayada1994.mydictionary_hybrid.viewmodels

import androidx.fragment.app.Fragment
import com.mayada1994.mydictionary_hybrid.R
import com.mayada1994.mydictionary_hybrid.utils.ViewEvent

class MainMenuViewModel : BaseViewModel() {

    sealed class MainMenuEvent {
        data class ShowSelectedScreen(
            val fragmentClass: Class<out Fragment>,
            val selectedMenuItemId: Int
        ) : ViewEvent
    }

    fun onMenuItemSelected(itemId: Int) {
        setEvent(
            when (itemId) {
//                R.id.dictionary_menu_item -> MainMenuEvent.ShowSelectedScreen(
//                    DictionaryFragment::class.java,
//                    selectedMenuItemId = 0
//                )
//
//                R.id.quiz_menu_item -> MainMenuEvent.ShowSelectedScreen(
//                    QuizFragment::class.java,
//                    selectedMenuItemId = 1
//                )
//
//                R.id.languages_menu_item -> MainMenuEvent.ShowSelectedScreen(
//                    DefaultLanguageFragment::class.java,
//                    selectedMenuItemId = 2
//                )
//
//                R.id.statistics_menu_item -> MainMenuEvent.ShowSelectedScreen(
//                    StatisticsFragment::class.java,
//                    selectedMenuItemId = 3
//                )

                else -> BaseEvent.ShowMessage(R.string.general_error)
            }
        )
    }

}