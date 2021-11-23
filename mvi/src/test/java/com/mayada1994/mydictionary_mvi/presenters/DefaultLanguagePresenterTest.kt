package com.mayada1994.mydictionary_mvi.presenters

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.interactors.DefaultLanguageInteractor
import com.mayada1994.mydictionary_mvi.states.DefaultLanguageState
import com.mayada1994.mydictionary_mvi.views.DefaultLanguageView
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

class DefaultLanguagePresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val view: DefaultLanguageView = mockk(relaxed = true)

    private val interactor: DefaultLanguageInteractor = mockk()

    private lateinit var presenter: DefaultLanguagePresenter

    @Before
    fun setup() {
        presenter = DefaultLanguagePresenter(interactor)
        presenter.bind(view)
    }

    @After
    fun clear() {
        presenter.unbind()
        unmockkAll()
    }

    /**
     * Given:
     * - displayLanguagesIntent in view returns observable with unit
     * - some add languages state
     * - getData in interactor returns add languages state
     * When:
     * - observeDisplayLanguagesIntent is called
     * Then should:
     * - call getData in interactor
     * - call render with add languages in view
     */
    @Test
    fun check_observeDisplayLanguagesIntent() {
        //Given
        val defaultLanguage = LanguageInfo(
            nameRes = R.string.italian_language,
            locale = "it",
            imageRes = R.drawable.ic_italy
        )

        val languageItems = listOf(
            LanguageInfo(
                nameRes = R.string.chinese_language,
                locale = "zh",
                imageRes = R.drawable.ic_china
            ),
            LanguageInfo(
                nameRes = R.string.italian_language,
                locale = "it",
                imageRes = R.drawable.ic_italy
            ),
            LanguageInfo(
                nameRes = R.string.japanese_language,
                locale = "ja",
                imageRes = R.drawable.ic_japan
            ),
            LanguageInfo(
                nameRes = R.string.polish_language,
                locale = "pl",
                imageRes = R.drawable.ic_poland
            ),
            LanguageInfo(
                nameRes = R.string.turkish_language,
                locale = "tr",
                imageRes = R.drawable.ic_turkey
            )
        ).map { it.toDefaultLanguageItem() }

        val state = DefaultLanguageState.DataState(
            defaultLanguage = defaultLanguage,
            languages = languageItems,
            isVisible = true
        )

        every { view.displayLanguagesIntent() } returns Observable.just(Unit)
        every { interactor.getData() } returns Observable.just(state)

        //When
        presenter.javaClass.getDeclaredMethod("observeDisplayLanguagesIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.getData()
            view.render(state)
        }
    }

    /**
     * Given:
     * - addButtonClickIntent in view returns observable with unit
     * - some add languages state
     * - onAddButtonClick in interactor returns add languages state
     * When:
     * - observeAddButtonClickIntent is called
     * Then should:
     * - call onAddButtonClick in interactor
     * - call render with add languages in view
     */
    @Test
    fun check_observeAddButtonClickIntent() {
        //Given
        val state = DefaultLanguageState.NavigateToAddLanguagesFragmentState(emptyList())

        every { view.addButtonClickIntent() } returns Observable.just(Unit)
        every { interactor.onAddButtonClick() } returns Observable.just(state)

        //When
        presenter.javaClass.getDeclaredMethod("observeAddButtonClickIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.onAddButtonClick()
            view.render(state)
        }
    }

    /**
     * Given:
     * - setDefaultLanguageIntent in view returns observable with default language item
     * - some add languages state
     * - setDefaultLanguage with default language item in interactor returns add languages state
     * When:
     * - observeSetDefaultLanguage is called
     * Then should:
     * - call setDefaultLanguage with default language item in interactor
     * - call render with add languages in view
     */
    @Test
    fun check_observeSetDefaultLanguage() {
        //Given
        val defaultLanguage = LanguageInfo(
            nameRes = R.string.italian_language,
            locale = "it",
            imageRes = R.drawable.ic_italy
        )

        val state = DefaultLanguageState.ToolbarState(defaultLanguage)

        every { view.setDefaultLanguageIntent() } returns Observable.just(defaultLanguage.toDefaultLanguageItem())
        every { interactor.setDefaultLanguage(any()) } returns Observable.just(state)

        //When
        presenter.javaClass.getDeclaredMethod("observeSetDefaultLanguage").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.setDefaultLanguage(defaultLanguage.toDefaultLanguageItem())
            view.render(state)
        }
    }
    
}