package com.mayada1994.mydictionary_mvp.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mayada1994.mydictionary_mvp.activities.MainActivity
import com.mayada1994.mydictionary_mvp.adapters.LanguagesAdapter
import com.mayada1994.mydictionary_mvp.contracts.AddLanguagesContract
import com.mayada1994.mydictionary_mvp.databinding.FragmentAddLanguagesBinding
import com.mayada1994.mydictionary_mvp.di.DictionaryComponent
import com.mayada1994.mydictionary_mvp.entities.Language
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.items.LanguageItem
import com.mayada1994.mydictionary_mvp.presenters.AddLanguagesPresenter

class AddLanguagesFragment : Fragment(), AddLanguagesContract.ViewInterface {

    private lateinit var binding: FragmentAddLanguagesBinding

    private val presenter = AddLanguagesPresenter(this, DictionaryComponent.languageDataSource, DictionaryComponent.cacheUtils)

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

        presenter.init(arguments?.getParcelableArrayList(LANGUAGES) ?: emptyList())
    }

    private fun initListeners() {
        binding.btnSave.setOnClickListener { presenter.onSaveButtonClick() }
    }

    override fun setLanguages(languages: List<LanguageItem>) {
        binding.languagesRecyclerView.adapter = LanguagesAdapter(
            languages,
            object : LanguagesAdapter.OnLanguageItemClickListener {
                override fun onClick(languages: List<LanguageInfo>) {
                    presenter.onLanguagesSelected(languages)
                }
            })
    }

    override fun setFragment(fragmentClass: Class<out Fragment>) {
        (requireActivity() as MainActivity).setFragment(fragmentClass)
    }

    override fun onBackPressed() {
        activity?.onBackPressed()
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