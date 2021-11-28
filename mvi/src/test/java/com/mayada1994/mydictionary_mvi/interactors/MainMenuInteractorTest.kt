package com.mayada1994.mydictionary_mvi.interactors

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.fragments.DefaultLanguageFragment
import com.mayada1994.mydictionary_mvi.fragments.DictionaryFragment
import com.mayada1994.mydictionary_mvi.fragments.QuizFragment
import com.mayada1994.mydictionary_mvi.fragments.StatisticsFragment
import com.mayada1994.mydictionary_mvi.states.MainMenuState
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainMenuInteractorTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private lateinit var interactor: MainMenuInteractor

    @Before
    fun setup() {
        interactor = MainMenuInteractor()
    }

    @After
    fun clear() {
        unmockkAll()
    }

    /**
     * When:
     * - getSelectedMenuItem is called with R.id.dictionary_menu_item as itemId
     * Then should:
     * - return MainMenuState.ScreenState with DictionaryFragment and 0 as selectedMenuItemId
     */
    @Test
    fun check_getSelectedMenuItem_DictionaryFragment() {
        //Given
        val itemId = R.id.dictionary_menu_item
        val state = MainMenuState.ScreenState(DictionaryFragment::class.java, 0)

        //When
        val result = interactor.getSelectedMenuItem(itemId)

        //Then
        result.test().assertResult(state)
    }

    /**
     * When:
     * - getSelectedMenuItem is called with R.id.quiz_menu_item as itemId
     * Then should:
     * - return MainMenuState.ScreenState with QuizFragment and 1 as selectedMenuItemId
     */
    @Test
    fun check_getSelectedMenuItem_QuizFragment() {
        //Given
        val itemId = R.id.quiz_menu_item
        val state = MainMenuState.ScreenState(QuizFragment::class.java, 1)

        //When
        val result = interactor.getSelectedMenuItem(itemId)

        //Then
        result.test().assertResult(state)
    }

    /**
     * When:
     * - getSelectedMenuItem is called with R.id.languages_menu_item as itemId
     * Then should:
     * - return MainMenuState.ScreenState with DefaultLanguageFragment and 2 as selectedMenuItemId
     */
    @Test
    fun check_getSelectedMenuItem_DefaultLanguageFragment() {
        //Given
        val itemId = R.id.languages_menu_item
        val state = MainMenuState.ScreenState(DefaultLanguageFragment::class.java, 2)

        //When
        val result = interactor.getSelectedMenuItem(itemId)

        //Then
        result.test().assertResult(state)
    }

    /**
     * When:
     * - getSelectedMenuItem is called with R.id.statistics_menu_item as itemId
     * Then should:
     * - return MainMenuState.ScreenState with StatisticsFragment and 3 as selectedMenuItemId
     */
    @Test
    fun check_getSelectedMenuItem_StatisticsFragment() {
        //Given
        val itemId = R.id.statistics_menu_item
        val state = MainMenuState.ScreenState(StatisticsFragment::class.java, 3)

        //When
        val result = interactor.getSelectedMenuItem(itemId)

        //Then
        result.test().assertResult(state)
    }

    /**
     * When:
     * - getSelectedMenuItem is called with other itemId
     * Then should:
     * - return MainMenuState.ErrorState with R.string.general_error as resId
     */
    @Test
    fun check_getSelectedMenuItem_Other() {
        //Given
        val itemId = R.id.list_item
        val state = MainMenuState.ErrorState(R.string.general_error)

        //When
        val result = interactor.getSelectedMenuItem(itemId)

        //Then
        result.test().assertResult(state)
    }

}