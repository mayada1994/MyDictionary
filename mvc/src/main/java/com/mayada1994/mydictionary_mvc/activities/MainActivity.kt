package com.mayada1994.mydictionary_mvc.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mayada1994.mydictionary_mvc.R
import com.mayada1994.mydictionary_mvc.databinding.ActivityMainBinding
import com.mayada1994.mydictionary_mvc.di.DictionaryComponent
import com.mayada1994.mydictionary_mvc.fragments.AddLanguagesFragment
import com.mayada1994.mydictionary_mvc.fragments.MainFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setInitialScreen()
    }

    private fun setInitialScreen() {
        if (DictionaryComponent.cacheUtils.defaultLanguage.isNullOrBlank()) {
            setFragment(AddLanguagesFragment::class.java)
        } else {
            setFragment(MainFragment::class.java)
        }
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