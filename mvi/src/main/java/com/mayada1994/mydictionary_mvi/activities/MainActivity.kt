package com.mayada1994.mydictionary_mvi.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.databinding.ActivityMainBinding
import com.mayada1994.mydictionary_mvi.di.DictionaryComponent
import com.mayada1994.mydictionary_mvi.interactors.MainInteractor
import com.mayada1994.mydictionary_mvi.presenters.MainPresenter
import com.mayada1994.mydictionary_mvi.states.MainState
import com.mayada1994.mydictionary_mvi.views.MainView
import io.reactivex.Observable

class MainActivity : AppCompatActivity(), MainView {

    private lateinit var binding: ActivityMainBinding

    private lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = MainPresenter(MainInteractor(DictionaryComponent.cacheUtils))
        presenter.bind(this)
    }

    override fun render(state: MainState) {
        when (state) {
            is MainState.ScreenState -> setFragment(state.fragmentClass)
        }
    }

    override fun displayInitialScreenIntent(): Observable<Unit> = Observable.just(Unit)

    fun setFragment(fragmentClass: Class<out Fragment>) {
        supportFragmentManager.commit {
            replace(R.id.main_container, fragmentClass.newInstance(), fragmentClass.simpleName)
        }
    }

    fun showProgress(visible: Boolean) {
        binding.progressBar.isVisible = visible
    }

    override fun onStop() {
        super.onStop()
        presenter.unbind()
    }

}