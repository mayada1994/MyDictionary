package com.mayada1994.mydictionary_mvvm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.mayada1994.mydictionary_mvvm.R
import com.mayada1994.mydictionary_mvvm.databinding.FragmentMainBinding
import com.mayada1994.mydictionary_mvvm.di.DictionaryComponent
import com.mayada1994.mydictionary_mvvm.viewmodels.MainMenuViewModel

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
        viewModel.selectedScreen.observe(viewLifecycleOwner, { selectedScreen ->
            showSelectedScreen(selectedScreen.fragmentClass, selectedScreen.selectedMenuItemId)
        })

        viewModel.toastMessageStringResId.observe(viewLifecycleOwner, { resId ->
            showMessage(resId)
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