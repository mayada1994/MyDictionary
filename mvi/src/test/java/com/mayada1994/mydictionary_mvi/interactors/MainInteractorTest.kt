package com.mayada1994.mydictionary_mvi.interactors

import com.mayada1994.mydictionary_mvi.fragments.AddLanguagesFragment
import com.mayada1994.mydictionary_mvi.fragments.MainFragment
import com.mayada1994.mydictionary_mvi.states.MainState
import com.mayada1994.mydictionary_mvi.utils.CacheUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainInteractorTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var interactor: MainInteractor

    @Before
    fun setup() {
        interactor = MainInteractor(cacheUtils)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    @Test
    fun `Given cached default language is null, when getInitialScreen is called, then should return ScreenState with AddLanguagesFragment`() {
        //Given
        val state = MainState.ScreenState(AddLanguagesFragment::class.java)

        every { cacheUtils.defaultLanguage } returns null

        //When
        val result = interactor.getInitialScreen()

        //Then
        result.test().assertResult(state)
    }

    @Test
    fun `Given cached default language is en, when getInitialScreen is called, then should return ScreenState with MainFragment`() {
        //Given
        val state = MainState.ScreenState(MainFragment::class.java)

        every { cacheUtils.defaultLanguage } returns "en"

        //When
        val result = interactor.getInitialScreen()

        //Then
        result.test().assertResult(state)
    }

}