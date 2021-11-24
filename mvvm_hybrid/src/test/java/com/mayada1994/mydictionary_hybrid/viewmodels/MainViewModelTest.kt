package com.mayada1994.mydictionary_hybrid.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.mydictionary_hybrid.fragments.AddLanguagesFragment
import com.mayada1994.mydictionary_hybrid.fragments.MainFragment
import com.mayada1994.mydictionary_hybrid.utils.CacheUtils
import com.mayada1994.mydictionary_hybrid.utils.ViewEvent
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

    private val observerViewEvent: Observer<ViewEvent> = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(cacheUtils)
        viewModel.event.observeForever(observerViewEvent)
        every { observerViewEvent.onChanged(any()) } just Runs
    }

    @After
    fun clear() {
        unmockkAll()
    }

    @Test
    fun `Given cached default language is null, when init is called, then should call setEvent with ShowSelectedScreen with AddLanguagesFragment`() {
        //Given
        every { cacheUtils.defaultLanguage } returns null

        //When
        viewModel.init()

        //Then
        verify { observerViewEvent.onChanged(MainViewModel.MainEvent.ShowSelectedScreen(AddLanguagesFragment::class.java)) }
    }

    @Test
    fun `Given cached default language is en, when init is called, then should call setEvent with ShowSelectedScreen with MainFragment`() {
        //Given
        every { cacheUtils.defaultLanguage } returns "en"

        //When
        viewModel.init()

        //Then
        verify { observerViewEvent.onChanged(MainViewModel.MainEvent.ShowSelectedScreen(MainFragment::class.java)) }
    }


}