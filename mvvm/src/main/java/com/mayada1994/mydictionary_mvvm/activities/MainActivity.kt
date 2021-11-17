package com.mayada1994.mydictionary_mvvm.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mayada1994.mydictionary_mvvm.R
import com.mayada1994.mydictionary_mvvm.databinding.ActivityMainBinding
import com.mayada1994.mydictionary_mvvm.di.DictionaryComponent
import com.mayada1994.mydictionary_mvvm.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> { DictionaryComponent.viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObservers()

        viewModel.init()
    }

    private fun initObservers() {
        viewModel.selectedScreen.observe(this, { fragmentClass ->
            setFragment(fragmentClass)
        })
    }

    fun setFragment(fragmentClass: Class<out Fragment>) {
        supportFragmentManager.commit {
            replace(R.id.main_container, fragmentClass.newInstance(), fragmentClass.simpleName)
        }
    }

    fun showProgress(visible: Boolean) {
        binding.progressBar.isVisible = visible
    }

}