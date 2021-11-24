package com.mayada1994.mydictionary_hybrid.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mayada1994.mydictionary_hybrid.activities.MainActivity
import com.mayada1994.mydictionary_hybrid.adapters.StatisticsAdapter
import com.mayada1994.mydictionary_hybrid.databinding.FragmentStatisticsBinding
import com.mayada1994.mydictionary_hybrid.decorators.WordsDecoration
import com.mayada1994.mydictionary_hybrid.di.DictionaryComponent
import com.mayada1994.mydictionary_hybrid.entities.LanguageInfo
import com.mayada1994.mydictionary_hybrid.entities.Statistics
import com.mayada1994.mydictionary_hybrid.events.BaseEvent
import com.mayada1994.mydictionary_hybrid.events.StatisticsEvent
import com.mayada1994.mydictionary_hybrid.viewmodels.StatisticsViewModel

class StatisticsFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding

    private val viewModel by viewModels<StatisticsViewModel> { DictionaryComponent.viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()

        viewModel.init()
    }

    private fun initObservers() {
        viewModel.event.observe(viewLifecycleOwner, { event ->
            when (event) {
                is StatisticsEvent.SetStats -> setStats(event.stats)

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
        viewModel.onDestroy()
    }

}