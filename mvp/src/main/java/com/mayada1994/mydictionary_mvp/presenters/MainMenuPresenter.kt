package com.mayada1994.mydictionary_mvp.presenters

import com.mayada1994.mydictionary_mvp.contracts.MainMenuContract
import timber.log.Timber

class MainMenuPresenter(
    private val viewInterface: MainMenuContract.ViewInterface
): MainMenuContract.PresenterInterface {

    override fun onMenuItemSelected(itemId: Int) {
        when (itemId) {
//            R.id.dictionary_menu_item -> viewInterface.showSelectedScreen(
//                DictionaryFragment::class.java,
//                selectedMenuItemId = 0
//            )
//            R.id.quiz_menu_item -> viewInterface.showSelectedScreen(
//                QuizFragment::class.java,
//                selectedMenuItemId = 1
//            )
//            R.id.languages_menu_item -> viewInterface.showSelectedScreen(
//                DefaultLanguageFragment::class.java,
//                selectedMenuItemId = 2
//            )
//            R.id.statistics_menu_item -> viewInterface.showSelectedScreen(
//                StatisticsFragment::class.java,
//                selectedMenuItemId = 3
//            )
            else -> Timber.e("Unknown menu item")
        }
    }

}