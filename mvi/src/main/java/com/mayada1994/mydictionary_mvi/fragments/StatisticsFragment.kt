package com.mayada1994.mydictionary_mvi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.mayada1994.mydictionary_mvi.activities.MainActivity
import com.mayada1994.mydictionary_mvi.adapters.StatisticsAdapter
import com.mayada1994.mydictionary_mvi.databinding.FragmentStatisticsBinding
import com.mayada1994.mydictionary_mvi.decorators.WordsDecoration
import com.mayada1994.mydictionary_mvi.di.DictionaryComponent
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.entities.Statistics
import com.mayada1994.mydictionary_mvi.interactors.StatisticsInteractor
import com.mayada1994.mydictionary_mvi.presenters.StatisticsPresenter
import com.mayada1994.mydictionary_mvi.states.StatisticsState
import com.mayada1994.mydictionary_mvi.views.StatisticsView
import io.reactivex.Observable

class StatisticsFragment : Fragment(), StatisticsView {

    private lateinit var binding: FragmentStatisticsBinding

    private lateinit var presenter: StatisticsPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = StatisticsPresenter(StatisticsInteractor(DictionaryComponent.statisticsRepository, DictionaryComponent.cacheUtils))
        presenter.bind(this)
    }

    override fun displayStatisticsIntent(): Observable<Unit> = Observable.just(Unit)

    override fun render(state: StatisticsState) {
        when (state) {
            is StatisticsState.DataState -> renderDataState(state.defaultLanguage, state.stats)

            is StatisticsState.LoadingState -> renderLoadingState()

            is StatisticsState.EmptyState -> renderEmptyState(state.defaultLanguage)

            is StatisticsState.ErrorState -> renderErrorState(state.defaultLanguage, state.resId)
        }
    }

    private fun renderDataState(defaultLanguage: LanguageInfo, stats: List<Statistics>) {
        showProgress(false)
        showPlaceholder(false)
        setToolbar(defaultLanguage)
        setStats(stats)
    }

    private fun renderLoadingState() {
        showProgress(true)
    }

    private fun renderEmptyState(defaultLanguage: LanguageInfo) {
        showProgress(false)
        showPlaceholder(true)
        setToolbar(defaultLanguage)
    }

    private fun renderErrorState(defaultLanguage: LanguageInfo, resId: Int) {
        showProgress(false)
        showPlaceholder(true)
        setToolbar(defaultLanguage)
        showMessage(resId)
    }

    private fun setToolbar(defaultLanguage: LanguageInfo) {
        with(binding) {
            toolbar.imgDefaultFlag.setImageResource(defaultLanguage.imageRes)
            toolbar.txtDefaultLanguage.text = getString(defaultLanguage.nameRes)
        }
    }

    private fun setStats(stats: List<Statistics>) {
        binding.statsRecyclerView.apply {
            adapter = StatisticsAdapter(stats)
            addItemDecoration(WordsDecoration(requireContext()))
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
        presenter.unbind()
    }

}