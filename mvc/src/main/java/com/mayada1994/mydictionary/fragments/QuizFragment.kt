package com.mayada1994.mydictionary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mayada1994.mydictionary.R
import com.mayada1994.mydictionary.activities.MainActivity
import com.mayada1994.mydictionary.adapters.QuizAdapter
import com.mayada1994.mydictionary.databinding.FragmentQuizBinding
import com.mayada1994.mydictionary.di.DictionaryComponent
import com.mayada1994.mydictionary.entities.Statistics
import com.mayada1994.mydictionary.entities.Word
import com.mayada1994.mydictionary.items.QuestionItem
import com.mayada1994.mydictionary.models.StatisticsDataSource
import com.mayada1994.mydictionary.models.WordDataSource
import com.mayada1994.mydictionary.utils.LanguageUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class QuizFragment : Fragment() {

    private lateinit var binding: FragmentQuizBinding

    private val compositeDisposable = CompositeDisposable()

    private lateinit var wordDataSource: WordDataSource

    private lateinit var statisticsDataSource: StatisticsDataSource

    private var defaultLanguage: String? = null

    companion object {
        private const val MIN_WORD_AMOUNT = 4
        private const val MAX_WORD_AMOUNT_PER_QUIZ = 20
        private const val WRONG_ANSWERS_AMOUNT_PER_QUIZ = 3
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wordDataSource = DictionaryComponent.wordDataSource

        statisticsDataSource = DictionaryComponent.statisticsDataSource

        initListeners()
        setToolbar()
    }

    private fun initListeners() {
        binding.btnResult.setOnClickListener { getResult() }
    }

    private fun setToolbar() {
        DictionaryComponent.cacheUtils.defaultLanguage?.let {
            defaultLanguage = it
            getWords(it)
            LanguageUtils.getLanguageByCode(it)?.let {
                with(binding) {
                    toolbar.imgDefaultFlag.setImageResource(it.imageRes)
                    toolbar.txtDefaultLanguage.text = getString(it.nameRes)
                }
            }
        }
    }

    private fun getWords(defaultLanguage: String) {
        (requireActivity() as MainActivity).showProgress(true)
        compositeDisposable.add(
            wordDataSource.getWordsByLanguage(defaultLanguage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { (requireActivity() as MainActivity).showProgress(false) }
                .subscribeWith(object : DisposableSingleObserver<List<Word>>() {
                    override fun onSuccess(words: List<Word>) {
                        if (words.size >= MIN_WORD_AMOUNT) {
                            showPlaceholder(false)
                            changeResultButtonVisibility(true)
                            setQuestions(words)
                        } else {
                            showPlaceholder(true)
                            changeResultButtonVisibility(false)
                        }
                    }

                    override fun onError(e: Throwable) {
                        showPlaceholder(true)
                        changeResultButtonVisibility(false)
                        showToast(R.string.general_error)
                    }
                })
        )
    }

    private fun setQuestions(words: List<Word>) {
        val questionItems = arrayListOf<QuestionItem>()

        words.shuffled().take(MAX_WORD_AMOUNT_PER_QUIZ).forEach { questionWord ->
            words.filterNot { it == questionWord }.shuffled().take(WRONG_ANSWERS_AMOUNT_PER_QUIZ)
                .map { it.translation }.let { wrongAnswers ->
                    questionItems.add(
                        QuestionItem(
                            questionWord,
                            arrayListOf(questionWord.translation).apply {
                                addAll(wrongAnswers)
                                shuffle()
                            })
                    )
                }
        }

        binding.questionsRecyclerView.adapter = QuizAdapter(questionItems)
    }

    private fun getResult() {
        (binding.questionsRecyclerView.adapter as QuizAdapter?)?.getItems()?.let { questionItems ->
            defaultLanguage?.let { language ->
                addResultToStats(
                    Statistics(
                        result = "${questionItems.filter { it.word.translation == it.selectedAnswer }.size}/${questionItems.size}",
                        timestamp = System.currentTimeMillis(),
                        language = language
                    )
                )
            }
        }
    }

    private fun addResultToStats(result: Statistics) {
        (requireActivity() as MainActivity).showProgress(true)
        compositeDisposable.add(
            statisticsDataSource.insertStatistics(result)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    (requireActivity() as MainActivity).showProgress(false)
                    showResultFragment(result.result)
                }
                .subscribeWith(object : DisposableCompletableObserver(){
                    override fun onComplete() {
                        Timber.d("Stat added successfully")
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e)
                    }
                })
        )
    }

    private fun showResultFragment(result: String) {
        parentFragmentManager.commit {
            defaultLanguage?.let {
                replace(
                    R.id.container,
                    ResultFragment.newInstance(result, it),
                    ResultFragment::class.java.simpleName
                )
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

}