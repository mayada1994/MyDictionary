package com.mayada1994.mydictionary_mvvm.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mayada1994.mydictionary_mvvm.activities.MainActivity
import com.mayada1994.mydictionary_mvvm.adapters.LanguagesAdapter
import com.mayada1994.mydictionary_mvvm.databinding.FragmentAddLanguagesBinding
import com.mayada1994.mydictionary_mvvm.di.DictionaryComponent
import com.mayada1994.mydictionary_mvvm.entities.Language
import com.mayada1994.mydictionary_mvvm.entities.LanguageInfo
import com.mayada1994.mydictionary_mvvm.items.LanguageItem
import com.mayada1994.mydictionary_mvvm.viewmodels.AddLanguagesViewModel

class AddLanguagesFragment : Fragment() {

    private lateinit var binding: FragmentAddLanguagesBinding

    private val viewModel by viewModels<AddLanguagesViewModel> { DictionaryComponent.viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddLanguagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initObservers()

        viewModel.init(arguments?.getParcelableArrayList(LANGUAGES) ?: emptyList())
    }

    private fun initListeners() {
        binding.btnSave.setOnClickListener { viewModel.onSaveButtonClick() }
    }

    private fun initObservers() {
        viewModel.languagesList.observe(viewLifecycleOwner, { languages ->
            setLanguages(languages)
        })

        viewModel.selectedScreen.observe(viewLifecycleOwner, { fragmentClass ->
            setFragment(fragmentClass)
        })

        viewModel.onBackPressed.observe(viewLifecycleOwner, {
            onBackPressed()
        })

        viewModel.isProgressVisible.observe(viewLifecycleOwner, { isVisible ->
            showProgress(isVisible)
        })

        viewModel.toastMessageStringResId.observe(viewLifecycleOwner, { resId ->
            showMessage(resId)
        })
    }

    private fun setLanguages(languages: List<LanguageItem>) {
        binding.languagesRecyclerView.adapter = LanguagesAdapter(
            languages,
            object : LanguagesAdapter.OnLanguageItemClickListener {
                override fun onClick(languages: List<LanguageInfo>) {
                    viewModel.onLanguagesSelected(languages)
                }
            })
    }

    private fun setFragment(fragmentClass: Class<out Fragment>) {
        (requireActivity() as MainActivity).setFragment(fragmentClass)
    }

    private fun onBackPressed() {
        activity?.onBackPressed()
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

    companion object {
        private const val LANGUAGES = "LANGUAGES"

        @JvmStatic
        fun newInstance(languages: List<Language>?) =
            AddLanguagesFragment().apply {
                arguments = Bundle().apply {
                    languages?.let {
                        putParcelableArrayList(LANGUAGES, it as ArrayList<out Parcelable>)
                    }
                }
            }
    }

}