package com.mayada1994.mydictionary_mvi.presenters

import com.mayada1994.mydictionary_mvi.fragments.AddLanguagesFragment
import com.mayada1994.mydictionary_mvi.interactors.MainInteractor
import com.mayada1994.mydictionary_mvi.states.MainState
import com.mayada1994.mydictionary_mvi.views.MainView
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

class MainPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val view: MainView = mockk(relaxed = true)

    private val interactor: MainInteractor = mockk()

    private lateinit var presenter: MainPresenter

    @Before
    fun setup() {
        presenter = MainPresenter(interactor)
        presenter.bind(view)
    }

    @After
    fun clear() {
        presenter.unbind()
        unmockkAll()
    }

    /**
     * Given:
     * - displayInitialScreenIntent in view returns observable with unit
     * - some main state
     * - getInitialScreen in interactor returns main state
     * When:
     * - observeDisplayInitialScreenIntent is called
     * Then should:
     * - call getInitialScreen in interactor
     * - call render with main state in view
     */
    @Test
    fun check_observeDisplayInitialScreenIntent() {
        //Given
        val state = MainState.ScreenState(fragmentClass = AddLanguagesFragment::class.java)
        every { view.displayInitialScreenIntent() } returns Observable.just(Unit)
        every { interactor.getInitialScreen() } returns Observable.just(state)

        //When
        presenter.javaClass.getDeclaredMethod("observeDisplayInitialScreenIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.getInitialScreen()
            view.render(state)
        }
    }
    
}