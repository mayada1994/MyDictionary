package com.mayada1994.mydictionary_mvi.interactors

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.fragments.DictionaryFragment
import com.mayada1994.mydictionary_mvi.fragments.QuizFragment
import com.mayada1994.mydictionary_mvi.states.MainMenuState
import io.reactivex.Observable

class MainMenuInteractor {

    fun getSelectedMenuItem(itemId: Int): Observable<MainMenuState> {
        return Observable.just(
            when (itemId) {
                R.id.dictionary_menu_item -> MainMenuState.ScreenState(
                    DictionaryFragment::class.java,
                    selectedMenuItemId = 0
                )

                R.id.quiz_menu_item -> MainMenuState.ScreenState(
                    QuizFragment::class.java,
                    selectedMenuItemId = 1
                )
//
//                R.id.languages_menu_item -> MainMenuState.ScreenState(
//                    DefaultLanguageFragment::class.java,
//                    selectedMenuItemId = 2
//                )
//
//                R.id.statistics_menu_item -> MainMenuState.ScreenState(
//                    StatisticsFragment::class.java,
//                    selectedMenuItemId = 3
//                )

                else -> MainMenuState.ErrorState(R.string.general_error)
            }
        )
    }

}