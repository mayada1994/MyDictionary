package com.mayada1994.mydictionary_mvi.presenters

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.fragments.DictionaryFragment
import com.mayada1994.mydictionary_mvi.interactors.MainMenuInteractor
import com.mayada1994.mydictionary_mvi.states.MainMenuState
import com.mayada1994.mydictionary_mvi.views.MainMenuView
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.Observable
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainMenuPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val view: MainMenuView = mockk(relaxed = true)

    private val interactor: MainMenuInteractor = mockk()

    private lateinit var presenter: MainMenuPresenter

    @Before
    fun setup() {
        presenter = MainMenuPresenter(interactor)
        presenter.bind(view)
    }

    @After
    fun clear() {
        presenter.unbind()
        unmockkAll()
    }

    /**
     * Given:
     * - selectMenuItemIntent in view returns observable with R.id.dictionary_menu_item
     * - some main menu state
     * - getSelectedMenuItem with R.id.dictionary_menu_item in interactor returns main menu state
     * When:
     * - observeSelectMenuItemIntent is called
     * Then should:
     * - call getSelectedMenuItem with R.id.dictionary_menu_item in interactor
     * - call render with main menu state in view
     */
    @Test
    fun check_observeSelectMenuItemIntent() {
        //Given
        val state = MainMenuState.ScreenState(
            fragmentClass = DictionaryFragment::class.java,
            selectedMenuItemId = 0
        )
        every { view.selectMenuItemIntent() } returns Observable.just(R.id.dictionary_menu_item)
        every { interactor.getSelectedMenuItem(R.id.dictionary_menu_item) } returns Observable.just(state)

        //When
        presenter.javaClass.getDeclaredMethod("observeSelectMenuItemIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.getSelectedMenuItem(R.id.dictionary_menu_item)
            view.render(state)
        }
    }
    
}