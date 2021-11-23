package com.mayada1994.mydictionary_mvi.presenters

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.entities.Language
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.interactors.AddLanguagesInteractor
import com.mayada1994.mydictionary_mvi.states.AddLanguagesState
import com.mayada1994.mydictionary_mvi.views.AddLanguagesView
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.Observable
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddLanguagesPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val view: AddLanguagesView = mockk(relaxed = true)

    private val interactor: AddLanguagesInteractor = mockk()

    private lateinit var presenter: AddLanguagesPresenter

    @Before
    fun setup() {
        presenter = AddLanguagesPresenter(interactor)
        presenter.bind(view)
    }

    @After
    fun clear() {
        presenter.unbind()
        unmockkAll()
    }

    /**
     * Given:
     * - displayLanguagesIntent in view returns observable with some list of languages
     * - some add languages state
     * - getLanguages with some list of languages in interactor returns add languages state
     * When:
     * - observeDisplayLanguagesIntent is called
     * Then should:
     * - call getLanguages with some list of languages in interactor
     * - call render with add languages in view
     */
    @Test
    fun check_observeDisplayLanguagesIntent() {
        //Given
        val usedLanguages = listOf(
            Language("en"),
            Language(("fr")),
            Language("ar"),
            Language("de"),
            Language("es"),
            Language(("ko"))
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
        ).map { it.toLanguageItem() }
        val state = AddLanguagesState.DataState(languageItems)

        every { view.displayLanguagesIntent() } returns Observable.just(usedLanguages)
        every { interactor.getLanguages(usedLanguages) } returns Observable.just(state)

        //When
        presenter.javaClass.getDeclaredMethod("observeDisplayLanguagesIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.getLanguages(usedLanguages)
            view.render(state)
        }
        assertEquals(false, presenter::class.java.getDeclaredField("initialScreen").apply { isAccessible = true }.get(presenter) as Boolean)
    }

    /**
     * Given:
     * - saveButtonClickIntent in view returns observable with unit
     * - some add languages state
     * - onSaveButtonClick in interactor returns add languages state
     * When:
     * - observeSaveButtonClickIntent is called
     * Then should:
     * - call onSaveButtonClick in interactor
     * - call render with add languages in view
     */
    @Test
    fun check_observeSaveButtonClickIntent() {
        //Given
        val state = AddLanguagesState.CompletedState(R.string.pick_languages_warning)

        every { view.saveButtonClickIntent() } returns Observable.just(Unit)
        every { interactor.onSaveButtonClick(any(), any()) } returns Observable.just(state)

        //When
        presenter.javaClass.getDeclaredMethod("observeSaveButtonClickIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.onSaveButtonClick(arrayListOf(), true)
            view.render(state)
        }
    }

    /**
     * Given:
     * - selectLanguagesIntent in view returns observable with some list of languageInfo
     * When:
     * - observeSelectLanguagesIntent is called
     * Then should:
     * - set private field selectedLanguages value as given list of languageInfo
     */
    @Test
    fun check_observeSelectLanguagesIntent() {
        //Given
        val languages = listOf(
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
        )
        every { view.selectLanguagesIntent() } returns Observable.just(languages)

        //When
        presenter.javaClass.getDeclaredMethod("observeSelectLanguagesIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        assertEquals(languages, presenter::class.java.getDeclaredField("selectedLanguages").apply { isAccessible = true }.get(presenter) as ArrayList<LanguageInfo>)
    }

}