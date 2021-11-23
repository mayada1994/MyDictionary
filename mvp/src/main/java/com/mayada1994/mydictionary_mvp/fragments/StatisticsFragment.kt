package com.mayada1994.mydictionary_mvp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.mayada1994.mydictionary_mvp.activities.MainActivity
import com.mayada1994.mydictionary_mvp.adapters.StatisticsAdapter
import com.mayada1994.mydictionary_mvp.contracts.StatisticsContract
import com.mayada1994.mydictionary_mvp.databinding.FragmentStatisticsBinding
import com.mayada1994.mydictionary_mvp.decorators.WordsDecoration
import com.mayada1994.mydictionary_mvp.di.DictionaryComponent
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.entities.Statistics
import com.mayada1994.mydictionary_mvp.presenters.StatisticsPresenter

class StatisticsFragment : Fragment(), StatisticsContract.ViewInterface {

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

        presenter = StatisticsPresenter(this, DictionaryComponent.statisticsDataSource, DictionaryComponent.cacheUtils)
        presenter.init()
    }

    override fun setToolbar(defaultLanguage: LanguageInfo) {
        with(binding) {
            toolbar.imgDefaultFlag.setImageResource(defaultLanguage.imageRes)
            toolbar.txtDefaultLanguage.text = getString(defaultLanguage.nameRes)
        }
    }

    override fun setStats(stats: List<Statistics>) {
        binding.statsRecyclerView.apply {
            adapter = StatisticsAdapter(stats)
            addItemDecoration(WordsDecoration(requireContext()))
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

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

}