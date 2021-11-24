package com.mayada1994.mydictionary_mvi.presenters

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.entities.Word
import com.mayada1994.mydictionary_mvi.interactors.QuizInteractor
import com.mayada1994.mydictionary_mvi.items.QuestionItem
import com.mayada1994.mydictionary_mvi.states.QuizState
import com.mayada1994.mydictionary_mvi.views.QuizView
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

class QuizPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val view: QuizView = mockk(relaxed = true)

    private val interactor: QuizInteractor = mockk()

    private lateinit var presenter: QuizPresenter

    @Before
    fun setup() {
        presenter = QuizPresenter(interactor)
        presenter.bind(view)
    }

    @After
    fun clear() {
        presenter.unbind()
        unmockkAll()
    }

    /**
     * Given:
     * - displayQuestionsIntent in view returns observable with unit
     * - some quiz state
     * - getData in interactor returns quiz state
     * When:
     * - observeDisplayQuestionsIntent is called
     * Then should:
     * - call getData in interactor
     * - call render with quiz state in view
     */
    @Test
    fun check_observeDisplayQuestionsIntent() {
        //Given
        val defaultLanguage = LanguageInfo(
            nameRes = R.string.italian_language,
            locale = "it",
            imageRes = R.drawable.ic_italy
        )

        val state = QuizState.EmptyState(defaultLanguage)
        every { view.displayQuestionsIntent() } returns Observable.just(Unit)
        every { interactor.getData() } returns Observable.just(state)

        //When
        presenter.javaClass.getDeclaredMethod("observeDisplayQuestionsIntent").run {
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
     * - displayResultIntent in view returns observable with list of question items
     * - some quiz state
     * - getResult with list of question items in interactor returns quiz state
     * When:
     * - observeDisplayResultIntent is called
     * Then should:
     * - call getResult with list of question items in interactor
     * - call render with quiz state in view
     */
    @Test
    fun check_observeDisplayResultIntent() {
        //Given
        val defaultLanguage = LanguageInfo(
            nameRes = R.string.english_language,
            locale = "en",
            imageRes = R.drawable.ic_england
        )

        val words = listOf(
            Word("cat", "кіт", defaultLanguage.locale),
            Word("dog", "пес", defaultLanguage.locale),
            Word("parrot", "папуга", defaultLanguage.locale),
            Word("turtle", "черепаха", defaultLanguage.locale)
        )

        val answers = arrayListOf("кіт", "пес", "папуга", "черепаха")

        val questionItems = listOf(
            QuestionItem(word = words[0], answers = answers, selectedAnswer = answers[1]),
            QuestionItem(word = words[1], answers = answers, selectedAnswer = answers[1]),
            QuestionItem(word = words[2], answers = answers, selectedAnswer = answers[0]),
            QuestionItem(word = words[3], answers = answers, selectedAnswer = answers[3])
        )

        val state = QuizState.EmptyState(defaultLanguage)
        every { view.displayResultIntent() } returns Observable.just(questionItems)
        every { interactor.getResult(any()) } returns Observable.just(state)

        //When
        presenter.javaClass.getDeclaredMethod("observeDisplayResultIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.getResult(questionItems)
            view.render(state)
        }
    }
    
}