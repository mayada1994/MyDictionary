package com.mayada1994.mydictionary_mvvm.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.mydictionary_mvvm.R
import com.mayada1994.mydictionary_mvvm.entities.SelectedScreen
import com.mayada1994.mydictionary_mvvm.fragments.DefaultLanguageFragment
import com.mayada1994.mydictionary_mvvm.fragments.DictionaryFragment
import com.mayada1994.mydictionary_mvvm.fragments.QuizFragment
import com.mayada1994.mydictionary_mvvm.fragments.StatisticsFragment
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

    private val observerSelectedScreen: Observer<SelectedScreen> = mockk()
    private val observerToastMessageStringResId: Observer<Int> = mockk()

    private lateinit var viewModel: MainMenuViewModel

    @Before
    fun setup() {
        viewModel = MainMenuViewModel()
        viewModel.selectedScreen.observeForever(observerSelectedScreen)
        viewModel.toastMessageStringResId.observeForever(observerToastMessageStringResId)
        every { observerSelectedScreen.onChanged(any()) } just Runs
        every { observerToastMessageStringResId.onChanged(any()) } just Runs
    }

    @After
    fun clear() {
        unmockkAll()
    }

    /**
     * When:
     * - onMenuItemSelected is called with R.id.dictionary_menu_item as itemId
     * Then should:
     * - post selectedScreen with instance of DictionaryFragment
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
            observerSelectedScreen.onChanged(
                SelectedScreen(
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
     * - post selectedScreen with instance of QuizFragment
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
            observerSelectedScreen.onChanged(
                SelectedScreen(
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
     * - post selectedScreen with instance of DefaultLanguageFragment
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
            observerSelectedScreen.onChanged(
                SelectedScreen(
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
     * - post selectedScreen with instance of StatisticsFragment
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
            observerSelectedScreen.onChanged(
                SelectedScreen(
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
     * - post toastMessageStringResId with resId as R.string.general_error
     */
    @Test
    fun check_onMenuItemSelected_Other() {
        //Given
        val itemId = R.id.list_item

        //When
        viewModel.onMenuItemSelected(itemId)

        //Then
        verify {
            observerToastMessageStringResId.onChanged(
                R.string.general_error
            )
        }
    }

}