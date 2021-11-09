package com.mayada1994.mydictionary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mayada1994.mydictionary.R
import com.mayada1994.mydictionary.databinding.FragmentMainBinding
import timber.log.Timber

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private var selectedMenuItemId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMenu()
    }

    override fun onResume() {
        super.onResume()

        binding.navigationView.menu.getItem(selectedMenuItemId)?.isChecked = true
    }

    private fun setMenu() {
        with(binding) {
            navigationView.menu.getItem(selectedMenuItemId)?.isChecked = true
            setFragmentWithoutAddingToBackStack(DictionaryFragment())

            navigationView.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.dictionary_menu_item -> {
                        selectedMenuItemId = 0
                        clearFragments()
                        setFragmentWithoutAddingToBackStack(DictionaryFragment())
                    }
                    R.id.quiz_menu_item -> {
                        selectedMenuItemId = 1
                        clearFragments()
                        setFragmentWithoutAddingToBackStack(QuizFragment())
                    }
                    R.id.languageContainer -> {
                        selectedMenuItemId = 2
                        clearFragments()
//                        setFragmentWithoutAddingToBackStack(LanguagesFragment())
                    }
                    R.id.statistics_menu_item -> {
                        selectedMenuItemId = 3
                        clearFragments()
                        setFragmentWithoutAddingToBackStack(StatisticsFragment())
                    }
                    else -> Timber.e("Unknown menu item")
                }
                true
            }
        }
    }

    fun setFragment(fragment: Fragment) {
        val current = getCurrentFragment()
        if (current != null && fragment.javaClass == current.javaClass) {
            return
        }
        parentFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.container, fragment, fragment::class.java.simpleName)
        }
    }

    private fun setFragmentWithoutAddingToBackStack(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.container, fragment, fragment::class.java.simpleName)
        }
    }

    private fun getCurrentFragment(): Fragment? {
        return parentFragmentManager.findFragmentById(R.id.container)
    }

    private fun clearFragments() {
        if (parentFragmentManager.backStackEntryCount > 0) {
            for (index in 0 until parentFragmentManager.backStackEntryCount) {
                parentFragmentManager.popBackStack()
            }
        }
    }

}