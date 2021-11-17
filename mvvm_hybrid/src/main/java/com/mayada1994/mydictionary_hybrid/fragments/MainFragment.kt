package com.mayada1994.mydictionary_hybrid.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.mayada1994.mydictionary_hybrid.R
import com.mayada1994.mydictionary_hybrid.databinding.FragmentMainBinding
import com.mayada1994.mydictionary_hybrid.di.DictionaryComponent
import com.mayada1994.mydictionary_hybrid.viewmodels.BaseViewModel.BaseEvent
import com.mayada1994.mydictionary_hybrid.viewmodels.MainMenuViewModel
import com.mayada1994.mydictionary_hybrid.viewmodels.MainMenuViewModel.MainMenuEvent

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private val viewModel by viewModels<MainMenuViewModel> { DictionaryComponent.viewModelFactory }

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
        initObservers()
    }

    override fun onResume() {
        super.onResume()

        binding.navigationView.menu.getItem(selectedMenuItemId)?.isChecked = true
    }

    private fun setMenu() {
        with(binding) {
            navigationView.menu.getItem(selectedMenuItemId)?.isChecked = true
//            setFragmentWithoutAddingToBackStack(DictionaryFragment())

            navigationView.setOnItemSelectedListener { menuItem ->
                viewModel.onMenuItemSelected(menuItem.itemId)
                true
            }
        }
    }

    private fun initObservers() {
        viewModel.event.observe(viewLifecycleOwner, { event ->
            when (event) {
                is MainMenuEvent.ShowSelectedScreen -> showSelectedScreen(
                    event.fragmentClass,
                    event.selectedMenuItemId
                )

                is BaseEvent.ShowMessage -> showMessage(event.resId)
            }
        })
    }

    private fun showSelectedScreen(
        fragmentClass: Class<out Fragment>,
        selectedMenuItemId: Int
    ) {
        this.selectedMenuItemId = selectedMenuItemId
        clearFragments()
        setFragmentWithoutAddingToBackStack(fragmentClass.newInstance())
    }

    private fun showMessage(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    private fun setFragmentWithoutAddingToBackStack(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.container, fragment, fragment::class.java.simpleName)
        }
    }

    private fun clearFragments() {
        if (parentFragmentManager.backStackEntryCount > 0) {
            for (index in 0 until parentFragmentManager.backStackEntryCount) {
                parentFragmentManager.popBackStack()
            }
        }
    }

}