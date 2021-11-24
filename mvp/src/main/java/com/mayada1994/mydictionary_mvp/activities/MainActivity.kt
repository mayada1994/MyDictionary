package com.mayada1994.mydictionary_mvp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.contracts.MainContract
import com.mayada1994.mydictionary_mvp.databinding.ActivityMainBinding
import com.mayada1994.mydictionary_mvp.di.DictionaryComponent
import com.mayada1994.mydictionary_mvp.presenters.MainPresenter

class MainActivity : AppCompatActivity(), MainContract.ViewInterface {

    private lateinit var binding: ActivityMainBinding

    private val presenter = MainPresenter(this, DictionaryComponent.cacheUtils)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter.init()
    }

    override fun setFragment(fragmentClass: Class<out Fragment>) {
        supportFragmentManager.commit {
            replace(R.id.main_container, fragmentClass.newInstance(), fragmentClass.simpleName)
        }
    }

    fun showProgress(visible: Boolean) {
        binding.progressBar.isVisible = visible
    }

}