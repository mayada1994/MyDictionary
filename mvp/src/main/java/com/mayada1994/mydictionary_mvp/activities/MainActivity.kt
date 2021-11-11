package com.mayada1994.mydictionary_mvp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.databinding.ActivityMainBinding

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
//        if (DictionaryComponent.cacheUtils.defaultLanguage.isNullOrBlank()) {
//            setFragment(AddLanguagesFragment.newInstance(null))
//        } else {
//            setFragment(MainFragment())
//        }
    }

    fun setFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.main_container, fragment, fragment::class.java.simpleName)
        }
    }

    fun showProgress(visible: Boolean) {
        binding.progressBar.isVisible = visible
    }

}