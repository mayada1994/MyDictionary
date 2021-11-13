package com.mayada1994.mydictionary_mvp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.contracts.MainMenuContract
import com.mayada1994.mydictionary_mvp.databinding.FragmentMainBinding
import com.mayada1994.mydictionary_mvp.presenters.MainMenuPresenter

class MainFragment : Fragment(), MainMenuContract.ViewInterface {

    private lateinit var binding: FragmentMainBinding

    private lateinit var presenter: MainMenuPresenter

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

        presenter = MainMenuPresenter(this)
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
                presenter.onMenuItemSelected(menuItem.itemId)
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

    override fun showSelectedScreen(
        fragmentClass: Class<out Fragment>,
        selectedMenuItemId: Int
    ) {
        this.selectedMenuItemId = selectedMenuItemId
        clearFragments()
        setFragmentWithoutAddingToBackStack(fragmentClass.newInstance())
    }

}