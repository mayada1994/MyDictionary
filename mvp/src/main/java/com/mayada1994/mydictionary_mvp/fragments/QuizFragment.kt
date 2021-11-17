package com.mayada1994.mydictionary_mvp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.activities.MainActivity
import com.mayada1994.mydictionary_mvp.adapters.QuizAdapter
import com.mayada1994.mydictionary_mvp.contracts.QuizContract
import com.mayada1994.mydictionary_mvp.databinding.FragmentQuizBinding
import com.mayada1994.mydictionary_mvp.di.DictionaryComponent
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.items.QuestionItem
import com.mayada1994.mydictionary_mvp.presenters.QuizPresenter

class QuizFragment : Fragment(), QuizContract.ViewInterface {

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

        presenter = QuizPresenter(this, DictionaryComponent.wordDataSource, DictionaryComponent.statisticsDataSource)
        presenter.init()

        initListeners()
    }

    private fun initListeners() {
        binding.btnResult.setOnClickListener { presenter.getResult((binding.questionsRecyclerView.adapter as QuizAdapter?)?.getItems()) }
    }

    override fun setToolbar(defaultLanguage: LanguageInfo) {
        with(binding) {
            toolbar.imgDefaultFlag.setImageResource(defaultLanguage.imageRes)
            toolbar.txtDefaultLanguage.text = getString(defaultLanguage.nameRes)
        }
    }

    override fun setQuestions(questions: List<QuestionItem>) {
        binding.questionsRecyclerView.adapter = QuizAdapter(questions)
    }

    override fun showResultFragment(result: String) {
        parentFragmentManager.commit {
                replace(
                    R.id.container,
                    ResultFragment.newInstance(result),
                    ResultFragment::class.java.simpleName
                )
        }
    }

    override fun showProgress(isVisible: Boolean) {
        (requireActivity() as MainActivity).showProgress(isVisible)
    }

    override fun showPlaceholder(isVisible: Boolean) {
        binding.txtPlaceholder.isVisible = isVisible
    }

    override fun showMessage(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    override fun changeResultButtonVisibility(isVisible: Boolean) {
        binding.btnResult.isVisible = isVisible
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

}