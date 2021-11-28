package com.mayada1994.mydictionary_mvi.presenters

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.interactors.ResultInteractor
import com.mayada1994.mydictionary_mvi.states.ResultState
import com.mayada1994.mydictionary_mvi.views.ResultView
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

class ResultPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val view: ResultView = mockk(relaxed = true)

    private val interactor: ResultInteractor = mockk()

    private lateinit var presenter: ResultPresenter

    @Before
    fun setup() {
        presenter = ResultPresenter(interactor)
        presenter.bind(view)
    }

    @After
    fun clear() {
        presenter.unbind()
        unmockkAll()
    }

    /**
     * Given:
     * - displayDataIntent in view returns observable with unit
     * - some result state
     * - getData in interactor returns result state
     * When:
     * - observeDisplayDataIntent is called
     * Then should:
     * - call getData in interactor
     * - call render with result state in view
     */
    @Test
    fun check_observeDisplayDataIntent() {
        //Given
        val defaultLanguage = LanguageInfo(
            nameRes = R.string.italian_language,
            locale = "it",
            imageRes = R.drawable.ic_italy
        )

        val state = ResultState.DataState(defaultLanguage)
        every { view.displayDataIntent() } returns Observable.just(Unit)
        every { interactor.getData() } returns Observable.just(state)

        //When
        presenter.javaClass.getDeclaredMethod("observeDisplayDataIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.getData()
            view.render(state)
        }
    }
    
}