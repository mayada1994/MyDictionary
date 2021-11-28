package com.mayada1994.mydictionary_mvvm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mayada1994.mydictionary_mvvm.activities.MainActivity
import com.mayada1994.mydictionary_mvvm.adapters.StatisticsAdapter
import com.mayada1994.mydictionary_mvvm.databinding.FragmentStatisticsBinding
import com.mayada1994.mydictionary_mvvm.decorators.WordsDecoration
import com.mayada1994.mydictionary_mvvm.di.DictionaryComponent
import com.mayada1994.mydictionary_mvvm.entities.LanguageInfo
import com.mayada1994.mydictionary_mvvm.entities.Statistics
import com.mayada1994.mydictionary_mvvm.viewmodels.StatisticsViewModel

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
        viewModel.defaultLanguage.observe(viewLifecycleOwner, { defaultLanguage ->
            setToolbar(defaultLanguage)
        })

        viewModel.statsList.observe(viewLifecycleOwner, { stats ->
            setStats(stats)
        })

        viewModel.isProgressVisible.observe(viewLifecycleOwner, { isVisible ->
            showProgress(isVisible)
        })

        viewModel.isPlaceholderVisible.observe(viewLifecycleOwner, { isVisible ->
            showPlaceholder(isVisible)
        })

        viewModel.toastMessageStringResId.observe(viewLifecycleOwner, { resId ->
            showMessage(resId)
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