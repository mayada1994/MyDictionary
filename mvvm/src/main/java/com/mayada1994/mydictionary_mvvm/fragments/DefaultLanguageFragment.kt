package com.mayada1994.mydictionary_mvvm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.mayada1994.mydictionary_mvvm.R
import com.mayada1994.mydictionary_mvvm.activities.MainActivity
import com.mayada1994.mydictionary_mvvm.adapters.DefaultLanguageListAdapter
import com.mayada1994.mydictionary_mvvm.databinding.FragmentDefaultLanguageBinding
import com.mayada1994.mydictionary_mvvm.di.DictionaryComponent
import com.mayada1994.mydictionary_mvvm.entities.Language
import com.mayada1994.mydictionary_mvvm.entities.LanguageInfo
import com.mayada1994.mydictionary_mvvm.items.DefaultLanguageItem
import com.mayada1994.mydictionary_mvvm.viewmodels.DefaultLanguageViewModel

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
        viewModel.defaultLanguage.observe(viewLifecycleOwner, { defaultLanguage ->
            setToolbar(defaultLanguage)
        })

        viewModel.languagesList.observe(viewLifecycleOwner, { languages ->
            setLanguages(languages)
        })

        viewModel.addButtonVisibility.observe(viewLifecycleOwner, { isVisible ->
            changeAddButtonVisibility(isVisible)
        })

        viewModel.navigateToAddLanguagesFragment.observe(viewLifecycleOwner, { languages ->
            navigateToAddLanguagesFragment(languages)
        })

        viewModel.isProgressVisible.observe(viewLifecycleOwner, { isVisible ->
            showProgress(isVisible)
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