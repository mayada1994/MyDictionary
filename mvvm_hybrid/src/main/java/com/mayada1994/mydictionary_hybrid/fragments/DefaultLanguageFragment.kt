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
import com.mayada1994.mydictionary_hybrid.adapters.DefaultLanguageListAdapter
import com.mayada1994.mydictionary_hybrid.databinding.FragmentDefaultLanguageBinding
import com.mayada1994.mydictionary_hybrid.di.DictionaryComponent
import com.mayada1994.mydictionary_hybrid.entities.Language
import com.mayada1994.mydictionary_hybrid.entities.LanguageInfo
import com.mayada1994.mydictionary_hybrid.events.BaseEvent
import com.mayada1994.mydictionary_hybrid.events.DefaultLanguageEvent
import com.mayada1994.mydictionary_hybrid.items.DefaultLanguageItem
import com.mayada1994.mydictionary_hybrid.viewmodels.DefaultLanguageViewModel

class DefaultLanguageFragment : Fragment() {

    private lateinit var binding: FragmentDefaultLanguageBinding

    private val viewModel by viewModels<DefaultLanguageViewModel> { DictionaryComponent.viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDefaultLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initObservers()

    }

    override fun onResume() {
        super.onResume()

        viewModel.init()
    }

    private fun initListeners() {
        binding.btnAdd.setOnClickListener { viewModel.onAddButtonClick() }
    }

    private fun initObservers() {
        viewModel.event.observe(viewLifecycleOwner, { event ->
            when (event) {
                is DefaultLanguageEvent.SetLanguages -> setLanguages(event.languages)

                is DefaultLanguageEvent.SetAddButtonVisibility -> changeAddButtonVisibility(event.isVisible)

                is DefaultLanguageEvent.NavigateToAddLanguagesFragment -> navigateToAddLanguagesFragment(event.languages)

                is BaseEvent.SetDefaultLanguage -> setToolbar(event.defaultLanguage)

                is BaseEvent.ShowProgress -> showProgress(event.isProgressVisible)

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

    private fun setLanguages(languages: List<DefaultLanguageItem>) {
        binding.languagesRecyclerView.adapter = DefaultLanguageListAdapter(
            languages,
            object : DefaultLanguageListAdapter.OnLanguageItemClickListener {
                override fun onClick(language: DefaultLanguageItem) {
                    (binding.languagesRecyclerView.adapter as DefaultLanguageListAdapter).setDefault(language)
                    viewModel.setDefaultLanguage(language)
                }
            })
    }

    private fun navigateToAddLanguagesFragment(languages: List<Language>) {
        parentFragmentManager.commit {
            replace(
                R.id.container,
                AddLanguagesFragment.newInstance(languages),
                AddLanguagesFragment::class.java.simpleName
            )
            addToBackStack(null)
        }
    }

    private fun changeAddButtonVisibility(isVisible: Boolean) {
        binding.btnAdd.isVisible = isVisible
    }

    private fun showProgress(isVisible: Boolean) {
        (requireActivity() as MainActivity).showProgress(isVisible)
    }

    private fun showMessage(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

}