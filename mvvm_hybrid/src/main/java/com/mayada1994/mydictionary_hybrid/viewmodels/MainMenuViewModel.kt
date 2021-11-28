package com.mayada1994.mydictionary_hybrid.viewmodels

import com.mayada1994.mydictionary_hybrid.R
import com.mayada1994.mydictionary_hybrid.events.BaseEvent
import com.mayada1994.mydictionary_hybrid.events.MainMenuEvent
import com.mayada1994.mydictionary_hybrid.fragments.DefaultLanguageFragment
import com.mayada1994.mydictionary_hybrid.fragments.DictionaryFragment
import com.mayada1994.mydictionary_hybrid.fragments.QuizFragment
import com.mayada1994.mydictionary_hybrid.fragments.StatisticsFragment

class MainMenuViewModel : BaseViewModel() {

    fun onMenuItemSelected(itemId: Int) {
        setEvent(
            when (itemId) {
                R.id.dictionary_menu_item -> MainMenuEvent.ShowSelectedScreen(
                    DictionaryFragment::class.java,
                    selectedMenuItemId = 0
                )

                R.id.quiz_menu_item -> MainMenuEvent.ShowSelectedScreen(
                    QuizFragment::class.java,
                    selectedMenuItemId = 1
                )

                R.id.languages_menu_item -> MainMenuEvent.ShowSelectedScreen(
                    DefaultLanguageFragment::class.java,
                    selectedMenuItemId = 2
                )

                R.id.statistics_menu_item -> MainMenuEvent.ShowSelectedScreen(
                    StatisticsFragment::class.java,
                    selectedMenuItemId = 3
                )

                else -> BaseEvent.ShowMessage(R.string.general_error)
            }
        )
    }

}