package com.mayada1994.mydictionary_mvi.presenters

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.entities.Statistics
import com.mayada1994.mydictionary_mvi.interactors.StatisticsInteractor
import com.mayada1994.mydictionary_mvi.states.StatisticsState
import com.mayada1994.mydictionary_mvi.views.StatisticsView
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

class StatisticsPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val view: StatisticsView = mockk(relaxed = true)

    private val interactor: StatisticsInteractor = mockk()

    private lateinit var presenter: StatisticsPresenter

    @Before
    fun setup() {
        presenter = StatisticsPresenter(interactor)
        presenter.bind(view)
    }

    @After
    fun clear() {
        presenter.unbind()
        unmockkAll()
    }

    /**
     * Given:
     * - displayStatisticsIntent in view returns observable with unit
     * - some statistics state
     * - getData in interactor returns statistics state
     * When:
     * - observeDisplayStatisticsIntent is called
     * Then should:
     * - call getData in interactor
     * - call render with statistics state in view
     */
    @Test
    fun check_observeDisplayStatisticsIntent() {
        //Given
        val languageInfo = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        val stats = listOf(
            Statistics(
                id = 0,
                result = "17/20",
                timestamp = System.currentTimeMillis(),
                language = languageInfo.locale
            )
        )

        val state = StatisticsState.DataState(languageInfo, stats)
        every { view.displayStatisticsIntent() } returns Observable.just(Unit)
        every { interactor.getData() } returns Observable.just(state)

        //When
        presenter.javaClass.getDeclaredMethod("observeDisplayStatisticsIntent").run {
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