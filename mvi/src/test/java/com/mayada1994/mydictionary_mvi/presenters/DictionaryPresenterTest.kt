package com.mayada1994.mydictionary_mvi.presenters

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.entities.Word
import com.mayada1994.mydictionary_mvi.interactors.DictionaryInteractor
import com.mayada1994.mydictionary_mvi.states.DictionaryState
import com.mayada1994.mydictionary_mvi.views.DictionaryView
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

class DictionaryPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val view: DictionaryView = mockk(relaxed = true)

    private val interactor: DictionaryInteractor = mockk()

    private lateinit var presenter: DictionaryPresenter

    @Before
    fun setup() {
        presenter = DictionaryPresenter(interactor)
        presenter.bind(view)
    }

    @After
    fun clear() {
        presenter.unbind()
        unmockkAll()
    }

    /**
     * Given:
     * - displayWordsIntent in view returns observable with unit
     * - some dictionary state
     * - getData in interactor returns dictionary state
     * When:
     * - observeDisplayWordsIntent is called
     * Then should:
     * - call getData in interactor
     * - call render with dictionary state in view
     */
    @Test
    fun check_observeDisplayWordsIntent() {
        //Given
        val words = listOf(Word("cat", "кіт", "en"))

        val defaultLanguage = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        val state = DictionaryState.DataState(defaultLanguage, words)
        every { view.displayWordsIntent() } returns Observable.just(Unit)
        every { interactor.getData() } returns Observable.just(state)

        //When
        presenter.javaClass.getDeclaredMethod("observeDisplayWordsIntent").run {
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
     * - some dictionary state
     * - onAddButtonClick in interactor returns dictionary state
     * When:
     * - observeAddButtonClickIntent is called
     * Then should:
     * - call onAddButtonClick in interactor
     * - call render with dictionary state in view
     */
    @Test
    fun check_observeAddButtonClickIntent() {
        //Given
        val state = DictionaryState.ShowAddNewWordDialogState
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
     * - saveButtonClickIntent in view returns observable with Pair<String?, String?>
     * - some dictionary state
     * - onSaveButtonClick in interactor returns dictionary state
     * When:
     * - observeSaveButtonClickIntent is called
     * Then should:
     * - call onSaveButtonClick in interactor
     * - call render with dictionary state in view
     */
    @Test
    fun check_observeSaveButtonClickIntent() {
        //Given
        val input = null to null
        val state = DictionaryState.CompletedState(R.string.fill_all_fields_prompt)
        every { view.saveButtonClickIntent() } returns Observable.just(input)
        every { interactor.onSaveButtonClick(input.first, input.second) } returns Observable.just(state)

        //When
        presenter.javaClass.getDeclaredMethod("observeSaveButtonClickIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.onSaveButtonClick(input.first, input.second)
            view.render(state)
        }
    }

    /**
     * Given:
     * - deleteButtonClickIntent in view returns observable with some word
     * - some dictionary state
     * - deleteWordFromDictionary in interactor returns dictionary state
     * When:
     * - observeDeleteButtonClickIntent is called
     * Then should:
     * - call deleteWordFromDictionary in interactor
     * - call render with dictionary state in view
     */
    @Test
    fun check_observeDeleteButtonClickIntent() {
        //Given
        val word = Word("cat", "кіт", "en")

        val defaultLanguage = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        val state = DictionaryState.DataState(defaultLanguage, listOf(word))
        every { view.deleteButtonClickIntent() } returns Observable.just(word)
        every { interactor.deleteWordFromDictionary(word) } returns Observable.just(state)

        //When
        presenter.javaClass.getDeclaredMethod("observeDeleteButtonClickIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.deleteWordFromDictionary(word)
            view.render(state)
        }
    }

}