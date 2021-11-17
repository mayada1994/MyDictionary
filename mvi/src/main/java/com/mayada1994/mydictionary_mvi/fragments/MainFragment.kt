package com.mayada1994.mydictionary_mvi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.databinding.FragmentMainBinding
import com.mayada1994.mydictionary_mvi.interactors.MainMenuInteractor
import com.mayada1994.mydictionary_mvi.presenters.MainMenuPresenter
import com.mayada1994.mydictionary_mvi.states.MainMenuState
import com.mayada1994.mydictionary_mvi.views.MainMenuView
import io.reactivex.Observable

class MainFragment : Fragment(), MainMenuView {

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

        presenter = MainMenuPresenter(MainMenuInteractor())
        presenter.bind(this)

        setMenu()
    }

    override fun onResume() {
        super.onResume()

        binding.navigationView.menu.getItem(selectedMenuItemId)?.isChecked = true
    }

    private fun setMenu() {
        binding.navigationView.menu.getItem(selectedMenuItemId)?.isChecked = true
        setFragmentWithoutAddingToBackStack(DictionaryFragment())
    }

    override fun render(state: MainMenuState) {
        when (state) {
            is MainMenuState.ScreenState -> renderScreenState(
                state.fragmentClass,
                state.selectedMenuItemId
            )

            is MainMenuState.ErrorState -> renderErrorState(state.resId)
        }
    }

    override fun selectMenuItemIntent(): Observable<Int> {
        return Observable.create { emitter ->
            binding.navigationView.setOnItemSelectedListener { menuItem ->
                emitter.onNext(menuItem.itemId)
                true
            }
        }
    }

    private fun renderScreenState(
        fragmentClass: Class<out Fragment>,
        selectedMenuItemId: Int
    ) {
        this.selectedMenuItemId = selectedMenuItemId
        clearFragments()
        setFragmentWithoutAddingToBackStack(fragmentClass.newInstance())
    }

    private fun renderErrorState(resId: Int) {
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

    override fun onStop() {
        super.onStop()
        presenter.unbind()
    }

}