package com.mayada1994.mydictionary.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mayada1994.mydictionary.databinding.FragmentAddLanguagesBinding
import com.mayada1994.mydictionary.entities.Language
import com.mayada1994.mydictionary.entities.LanguageInfo
import com.mayada1994.mydictionary.utils.LanguageUtils

class AddLanguagesFragment : Fragment() {

    private lateinit var binding: FragmentAddLanguagesBinding

    private var languages: List<LanguageInfo>? = null
    private var initialScreen: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getParcelableArrayList<Language>(LANGUAGES)?.let { usedLanguages ->
            languages = LanguageUtils.getLanguages().filterNot { it.locale in usedLanguages.map { it.code } }
            initialScreen = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddLanguagesBinding.inflate(inflater, container, false)
        return binding.root
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