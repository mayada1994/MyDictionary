package com.mayada1994.mydictionary_hybrid.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.mayada1994.mydictionary_hybrid.R
import com.mayada1994.mydictionary_hybrid.activities.MainActivity
import com.mayada1994.mydictionary_hybrid.adapters.QuizAdapter
import com.mayada1994.mydictionary_hybrid.databinding.FragmentQuizBinding
import com.mayada1994.mydictionary_hybrid.di.DictionaryComponent
import com.mayada1994.mydictionary_hybrid.entities.LanguageInfo
import com.mayada1994.mydictionary_hybrid.items.QuestionItem
import com.mayada1994.mydictionary_hybrid.viewmodels.BaseViewModel.BaseEvent
import com.mayada1994.mydictionary_hybrid.viewmodels.QuizViewModel
import com.mayada1994.mydictionary_hybrid.viewmodels.QuizViewModel.QuizEvent

class QuizFragment : Fragment() {

    private lateinit var binding: FragmentQuizBinding

    private val viewModel by viewModels<QuizViewModel> { DictionaryComponent.viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initObservers()

        viewModel.init()

    }

    private fun initListeners() {
        binding.btnResult.setOnClickListener { viewModel.getResult((binding.questionsRecyclerView.adapter as QuizAdapter?)?.getItems()) }
    }

    private fun initObservers() {
        viewModel.event.observe(viewLifecycleOwner, { event ->
            when (event) {
                is QuizEvent.SetQuestions -> setQuestions(event.questions)

                is QuizEvent.SetResult -> showResultFragment(event.result)

                is BaseEvent.SetDefaultLanguage -> setToolbar(event.defaultLanguage)

                is BaseEvent.ShowProgress -> showProgress(event.isProgressVisible)

                is BaseEvent.ShowPlaceholder -> showPlaceholder(event.isVisible)

                is BaseEvent.ShowMessage -> showMessage(event.resId)
            }
        })
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

    private fun showResultFragment(result: String) {
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

    private fun showMessage(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

}