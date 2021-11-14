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
import com.mayada1994.mydictionary_mvp.adapters.DefaultLanguageListAdapter
import com.mayada1994.mydictionary_mvp.contracts.DefaultLanguageContract
import com.mayada1994.mydictionary_mvp.databinding.FragmentDefaultLanguageBinding
import com.mayada1994.mydictionary_mvp.di.DictionaryComponent
import com.mayada1994.mydictionary_mvp.entities.Language
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.items.DefaultLanguageItem
import com.mayada1994.mydictionary_mvp.presenters.DefaultLanguagePresenter

class DefaultLanguageFragment : Fragment(), DefaultLanguageContract.ViewInterface {

    private lateinit var binding: FragmentDefaultLanguageBinding

    private lateinit var presenter: DefaultLanguagePresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDefaultLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = DefaultLanguagePresenter(this, DictionaryComponent.languageDataSource)

        setListeners()
    }

    override fun onResume() {
        super.onResume()

        presenter.init()
    }

    private fun setListeners() {
        binding.btnAdd.setOnClickListener { presenter.onAddButtonClick() }
    }

    override fun setToolbar(defaultLanguage: LanguageInfo) {
        with(binding) {
            toolbar.imgDefaultFlag.setImageResource(defaultLanguage.imageRes)
            toolbar.txtDefaultLanguage.text = getString(defaultLanguage.nameRes)
        }
    }

    override fun setLanguages(languages: List<DefaultLanguageItem>) {
        binding.languagesRecyclerView.adapter = DefaultLanguageListAdapter(
            languages,
            object : DefaultLanguageListAdapter.OnLanguageItemClickListener {
                override fun onClick(language: DefaultLanguageItem) {
                    (binding.languagesRecyclerView.adapter as DefaultLanguageListAdapter).setDefault(language)
                    presenter.setDefaultLanguage(language)
                }
            })
    }

    override fun navigateToAddLanguagesFragment(languages: List<Language>) {
        parentFragmentManager.commit {
            replace(
                R.id.container,
                AddLanguagesFragment.newInstance(languages),
                AddLanguagesFragment::class.java.simpleName
            )
            addToBackStack(null)
        }
    }

    override fun changeAddButtonVisibility(isVisible: Boolean) {
        binding.btnAdd.isVisible = isVisible
    }

    override fun showProgress(isVisible: Boolean) {
        (requireActivity() as MainActivity).showProgress(isVisible)
    }

    override fun showMessage(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

}