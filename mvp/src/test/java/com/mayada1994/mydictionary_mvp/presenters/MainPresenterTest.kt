package com.mayada1994.mydictionary_mvp.presenters

import com.mayada1994.mydictionary_mvp.contracts.MainContract
import com.mayada1994.mydictionary_mvp.fragments.AddLanguagesFragment
import com.mayada1994.mydictionary_mvp.fragments.MainFragment
import com.mayada1994.mydictionary_mvp.utils.CacheUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val viewInterface: MainContract.ViewInterface = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var presenter: MainPresenter

    @Before
    fun setup() {
        presenter = MainPresenter(viewInterface, cacheUtils)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    @Test
    fun `Given cached default language is null, when init is called, the should call setFragment in viewInterface with AddLanguagesFragment`() {
        //Given
        every { cacheUtils.defaultLanguage } returns null

        every { viewInterface.setFragment(any()) } just Runs

        //When
        presenter.init()

        //Then
        verify { viewInterface.setFragment(AddLanguagesFragment::class.java) }
    }

    @Test
    fun `Given cached default language is en, when init is called, the should call setFragment in viewInterface with MainFragment`() {
        //Given
        every { cacheUtils.defaultLanguage } returns "en"

        every { viewInterface.setFragment(any()) } just Runs

        //When
        presenter.init()

        //Then
        verify { viewInterface.setFragment(MainFragment::class.java) }
    }

}