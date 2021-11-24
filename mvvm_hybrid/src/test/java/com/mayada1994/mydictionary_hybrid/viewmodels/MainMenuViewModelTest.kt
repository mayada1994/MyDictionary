package com.mayada1994.mydictionary_hybrid.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.mydictionary_hybrid.R
import com.mayada1994.mydictionary_hybrid.fragments.DefaultLanguageFragment
import com.mayada1994.mydictionary_hybrid.fragments.DictionaryFragment
import com.mayada1994.mydictionary_hybrid.fragments.QuizFragment
import com.mayada1994.mydictionary_hybrid.fragments.StatisticsFragment
import com.mayada1994.mydictionary_hybrid.utils.ViewEvent
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class MainMenuViewModelTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val observerViewEvent: Observer<ViewEvent> = mockk()

    private lateinit var viewModel: MainMenuViewModel

    @Before
    fun setup() {
        viewModel = MainMenuViewModel()
        viewModel.event.observeForever(observerViewEvent)
        every { observerViewEvent.onChanged(any()) } just Runs
    }

    @After
    fun clear() {
        unmockkAll()
    }

    /**
     * When:
     * - onMenuItemSelected is called with R.id.dictionary_menu_item as itemId
     * Then should:
     * - call setEvent with ShowSelectedScreen with instance of DictionaryFragment
     */
    @Test
    fun check_onMenuItemSelected_DictionaryFragment() {
        //Given
        val itemId = R.id.dictionary_menu_item
        val fragmentClass = DictionaryFragment::class.java

        //When
        viewModel.onMenuItemSelected(itemId)

        //Then
        verify {
            observerViewEvent.onChanged(
                MainMenuViewModel.MainMenuEvent.ShowSelectedScreen(
                    fragmentClass = fragmentClass,
                    selectedMenuItemId = 0
                )
            )
        }
    }

    /**
     * When:
     * - onMenuItemSelected is called with R.id.quiz_menu_item as itemId
     * Then should:
     * - call setEvent with ShowSelectedScreen with instance of QuizFragment
     */
    @Test
    fun check_onMenuItemSelected_QuizFragment() {
        //Given
        val itemId = R.id.quiz_menu_item
        val fragmentClass = QuizFragment::class.java

        //When
        viewModel.onMenuItemSelected(itemId)

        //Then
        verify {
            observerViewEvent.onChanged(
                MainMenuViewModel.MainMenuEvent.ShowSelectedScreen(
                    fragmentClass = fragmentClass,
                    selectedMenuItemId = 1
                )
            )
        }
    }

    /**
     * When:
     * - onMenuItemSelected is called with R.id.languages_menu_item as itemId
     * Then should:
     * - call setEvent with ShowSelectedScreen with instance of DefaultLanguageFragment
     */
    @Test
    fun check_onMenuItemSelected_DefaultLanguageFragment() {
        //Given
        val itemId = R.id.languages_menu_item
        val fragmentClass = DefaultLanguageFragment::class.java

        //When
        viewModel.onMenuItemSelected(itemId)

        //Then
        verify {
            observerViewEvent.onChanged(
                MainMenuViewModel.MainMenuEvent.ShowSelectedScreen(
                    fragmentClass = fragmentClass,
                    selectedMenuItemId = 2
                )
            )
        }
    }

    /**
     * When:
     * - onMenuItemSelected is called with R.id.statistics_menu_item as itemId
     * Then should:
     * - call setEvent with ShowSelectedScreen with instance of StatisticsFragment
     */
    @Test
    fun check_onMenuItemSelected_StatisticsFragment() {
        //Given
        val itemId = R.id.statistics_menu_item
        val fragmentClass = StatisticsFragment::class.java

        //When
        viewModel.onMenuItemSelected(itemId)

        //Then
        verify {
            observerViewEvent.onChanged(
                MainMenuViewModel.MainMenuEvent.ShowSelectedScreen(
                    fragmentClass = fragmentClass,
                    selectedMenuItemId = 3
                )
            )
        }
    }

    /**
     * When:
     * - onMenuItemSelected is called with other itemId
     * Then should:
     * - call setEvent with ShowMessage with resId as R.string.general_error
     */
    @Test
    fun check_onMenuItemSelected_Other() {
        //Given
        val itemId = R.id.list_item

        //When
        viewModel.onMenuItemSelected(itemId)

        //Then
        verify {
            observerViewEvent.onChanged(
                BaseViewModel.BaseEvent.ShowMessage(R.string.general_error)
            )
        }
    }

}