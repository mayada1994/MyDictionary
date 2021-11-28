package com.mayada1994.mydictionary_mvvm.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mayada1994.mydictionary_mvvm.fragments.AddLanguagesFragment
import com.mayada1994.mydictionary_mvvm.fragments.MainFragment
import com.mayada1994.mydictionary_mvvm.utils.CacheUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class MainViewModelTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val observerSelectedScreen: Observer<Class<out Fragment>> = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(cacheUtils)
        viewModel.selectedScreen.observeForever(observerSelectedScreen)
        every { observerSelectedScreen.onChanged(any()) } just Runs
    }

    @After
    fun clear() {
        unmockkAll()
    }

    @Test
    fun `Given cached default language is null, when init is called, then should post selectedScreen with AddLanguagesFragment`() {
        //Given
        every { cacheUtils.defaultLanguage } returns null

        //When
        viewModel.init()

        //Then
        verify { observerSelectedScreen.onChanged(AddLanguagesFragment::class.java) }
    }

    @Test
    fun `Given cached default language is en, when init is called, then should post selectedScreen with MainFragment`() {
        //Given
        every { cacheUtils.defaultLanguage } returns "en"

        //When
        viewModel.init()

        //Then
        verify { observerSelectedScreen.onChanged(MainFragment::class.java) }
    }


}