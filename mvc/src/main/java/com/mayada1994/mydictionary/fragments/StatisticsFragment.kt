package com.mayada1994.mydictionary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.mayada1994.mydictionary.R
import com.mayada1994.mydictionary.activities.MainActivity
import com.mayada1994.mydictionary.adapters.StatisticsAdapter
import com.mayada1994.mydictionary.databinding.FragmentStatisticsBinding
import com.mayada1994.mydictionary.decorators.WordsDecoration
import com.mayada1994.mydictionary.di.DictionaryComponent
import com.mayada1994.mydictionary.entities.Statistics
import com.mayada1994.mydictionary.models.StatisticsDataSource
import com.mayada1994.mydictionary.utils.LanguageUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class StatisticsFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding

    private val compositeDisposable = CompositeDisposable()

    private lateinit var statisticsDataSource: StatisticsDataSource

    private var defaultLanguage: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statisticsDataSource = DictionaryComponent.statisticsDataSource

        setToolbar()
    }

    private fun setToolbar() {
        DictionaryComponent.cacheUtils.defaultLanguage?.let {
            defaultLanguage = it
            getStats(it)
            LanguageUtils.getLanguageByCode(it)?.let {
                with(binding) {
                    toolbar.imgDefaultFlag.setImageResource(it.imageRes)
                    toolbar.txtDefaultLanguage.text = getString(it.nameRes)
                }
            }
        }
    }

    private fun getStats(language: String) {
        (requireActivity() as MainActivity).showProgress(true)
        compositeDisposable.add(
            statisticsDataSource.getStatisticsByLanguage(language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { (requireActivity() as MainActivity).showProgress(false) }
                .subscribeWith(object : DisposableSingleObserver<List<Statistics>>() {
                    override fun onSuccess(statistics: List<Statistics>) {
                        if (statistics.isNotEmpty()) {
                            showPlaceholder(false)
                            setStats(statistics)
                        } else {
                            showPlaceholder(true)
                        }
                    }

                    override fun onError(e: Throwable) {
                        showPlaceholder(true)
                        showToast(R.string.general_error)
                    }
                })
        )
    }

    private fun setStats(stats: List<Statistics>) {
        binding.statsRecyclerView.apply {
            adapter = StatisticsAdapter(stats)
            addItemDecoration(WordsDecoration(requireContext()))
        }
    }

    private fun showPlaceholder(isVisible: Boolean) {
        binding.txtPlaceholder.isVisible = isVisible
    }

    private fun showToast(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

}