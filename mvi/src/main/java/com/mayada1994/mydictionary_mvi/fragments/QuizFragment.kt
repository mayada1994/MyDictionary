package com.mayada1994.mydictionary_mvi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.activities.MainActivity
import com.mayada1994.mydictionary_mvi.adapters.QuizAdapter
import com.mayada1994.mydictionary_mvi.databinding.FragmentQuizBinding
import com.mayada1994.mydictionary_mvi.di.DictionaryComponent
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.interactors.QuizInteractor
import com.mayada1994.mydictionary_mvi.items.QuestionItem
import com.mayada1994.mydictionary_mvi.presenters.QuizPresenter
import com.mayada1994.mydictionary_mvi.states.QuizState
import com.mayada1994.mydictionary_mvi.views.QuizView
import io.reactivex.Observable

class QuizFragment : Fragment(), QuizView {

    private lateinit var binding: FragmentQuizBinding

    private lateinit var presenter: QuizPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = QuizPresenter(QuizInteractor(DictionaryComponent.wordRepository, DictionaryComponent.statisticsRepository))
        presenter.bind(this)
    }

    override fun render(state: QuizState) {
        when (state) {
            is QuizState.DataState -> renderDataState(state.defaultLanguage, state.questions)

            is QuizState.ResultState -> renderResult(state.result)

            is QuizState.LoadingState -> renderLoadingState()

            is QuizState.EmptyState -> renderEmptyState(state.defaultLanguage)

            is QuizState.CompletedState -> renderCompletedState(state.resId)

            is QuizState.ErrorState -> renderErrorState(state.resId)
        }
    }

    override fun displayQuestionsIntent(): Observable<Unit> = Observable.just(Unit)

    override fun displayResultIntent(): Observable<List<QuestionItem>?> {
        return Observable.create { emitter ->
            binding.btnResult.setOnClickListener {
                emitter.onNext(
                    (binding.questionsRecyclerView.adapter as QuizAdapter?)?.getItems()
                        ?: emptyList()
                )
            }
        }
    }

    private fun renderDataState(defaultLanguage: LanguageInfo, questions: List<QuestionItem>) {
        showPlaceholder(false)
        showProgress(false)
        changeResultButtonVisibility(true)

        setToolbar(defaultLanguage)
        setQuestions(questions)
    }

    private fun renderLoadingState() {
        showProgress(true)
    }

    private fun renderEmptyState(defaultLanguage: LanguageInfo) {
        showProgress(false)
        showPlaceholder(true)
        changeResultButtonVisibility(false)
        setToolbar(defaultLanguage)
    }

    private fun renderCompletedState(resId: Int) {
        showProgress(false)
        showPlaceholder(false)
        changeResultButtonVisibility(true)
        showToast(resId)
    }

    private fun renderErrorState(resId: Int) {
        showProgress(false)
        showPlaceholder(true)
        changeResultButtonVisibility(false)
        showToast(resId)
    }

    private fun setToolbar(defaultLanguage: LanguageInfo) {
        with(binding) {
            toolbar.imgDefaultFlag.setImageResource(defaultLanguage.imageRes)
            toolbar.txtDefaultLanguage.text = getString(defaultLanguage.nameRes)
        }
    }

    private fun setQuestions(questions: List<QuestionItem>) {
        binding.questionsRecyclerView.adapter = QuizAdapter(questions)
    }

    private fun renderResult(result: String) {
        parentFragmentManager.commit {
            replace(
                R.id.container,
                ResultFragment.newInstance(result),
                ResultFragment::class.java.simpleName
            )
        }
    }

    private fun showProgress(isVisible: Boolean) {
        (requireActivity() as MainActivity).showProgress(isVisible)
    }

    private fun showPlaceholder(isVisible: Boolean) {
        binding.txtPlaceholder.isVisible = isVisible
    }

    private fun showToast(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    private fun changeResultButtonVisibility(isVisible: Boolean) {
        binding.btnResult.isVisible = isVisible
    }

    override fun onStop() {
        super.onStop()
        presenter.unbind()
    }

}