package com.mayada1994.mydictionary_mvp.presenters

import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.contracts.MainMenuContract
import com.mayada1994.mydictionary_mvp.fragments.DefaultLanguageFragment
import com.mayada1994.mydictionary_mvp.fragments.DictionaryFragment
import com.mayada1994.mydictionary_mvp.fragments.QuizFragment
import com.mayada1994.mydictionary_mvp.fragments.StatisticsFragment
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainMenuPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val viewInterface: MainMenuContract.ViewInterface = mockk()

    private lateinit var presenter: MainMenuPresenter

    @Before
    fun setup() {
        presenter = MainMenuPresenter(viewInterface)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    /**
     * When:
     * - onMenuItemSelected is called with R.id.dictionary_menu_item as itemId
     * Then should:
     * - call showSelectedScreen in viewInterface with instance of DictionaryFragment
     */
    @Test
    fun check_onMenuItemSelected_DictionaryFragment() {
        //Given
        val itemId = R.id.dictionary_menu_item
        val fragmentClass = DictionaryFragment::class.java
        every { viewInterface.showSelectedScreen(fragmentClass, 0) } just Runs

        //When
        presenter.onMenuItemSelected(itemId)

        //Then
        verify {
            viewInterface.showSelectedScreen(fragmentClass = fragmentClass, selectedMenuItemId = 0)
        }
    }

    /**
     * When:
     * - onMenuItemSelected is called with R.id.quiz_menu_item as itemId
     * Then should:
     * - call showSelectedScreen in viewInterface with instance of QuizFragment
     */
    @Test
    fun check_onMenuItemSelected_QuizFragment() {
        //Given
        val itemId = R.id.quiz_menu_item
        val fragmentClass = QuizFragment::class.java
        every { viewInterface.showSelectedScreen(fragmentClass, 1) } just Runs

        //When
        presenter.onMenuItemSelected(itemId)

        //Then
        verify {
            viewInterface.showSelectedScreen(fragmentClass = fragmentClass, selectedMenuItemId = 1)
        }
    }

    /**
     * When:
     * - onMenuItemSelected is called with R.id.languages_menu_item as itemId
     * Then should:
     * - call showSelectedScreen in viewInterface with instance of DefaultLanguageFragment
     */
    @Test
    fun check_onMenuItemSelected_DefaultLanguageFragment() {
        //Given
        val itemId = R.id.languages_menu_item
        val fragmentClass = DefaultLanguageFragment::class.java
        every { viewInterface.showSelectedScreen(fragmentClass, 2) } just Runs

        //When
        presenter.onMenuItemSelected(itemId)

        //Then
        verify {
            viewInterface.showSelectedScreen(fragmentClass = fragmentClass, selectedMenuItemId = 2)
        }
    }

    /**
     * When:
     * - onMenuItemSelected is called with R.id.statistics_menu_item as itemId
     * Then should:
     * - call showSelectedScreen in viewInterface with instance of StatisticsFragment
     */
    @Test
    fun check_onMenuItemSelected_StatisticsFragment() {
        //Given
        val itemId = R.id.statistics_menu_item
        val fragmentClass = StatisticsFragment::class.java
        every { viewInterface.showSelectedScreen(fragmentClass, 3) } just Runs

        //When
        presenter.onMenuItemSelected(itemId)

        //Then
        verify {
            viewInterface.showSelectedScreen(fragmentClass = fragmentClass, selectedMenuItemId = 3)
        }
    }

    /**
     * When:
     * - onMenuItemSelected is called with other itemId
     * Then should:
     * - not call showSelectedScreen in viewInterface
     */
    @Test
    fun check_onMenuItemSelected_Other() {
        //Given
        val itemId = R.id.list_item

        //When
        presenter.onMenuItemSelected(itemId)

        //Then
        verify(exactly = 0) {
            viewInterface.showSelectedScreen(fragmentClass = any(), selectedMenuItemId =any())
        }
    }
    
}