package com.mayada1994.mydictionary_mvvm.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mayada1994.mydictionary_mvvm.repositories.LanguageRepository
import com.mayada1994.mydictionary_mvvm.repositories.StatisticsRepository
import com.mayada1994.mydictionary_mvvm.repositories.WordRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val languageRepository: LanguageRepository,
    private val statisticsRepository: StatisticsRepository,
    private val wordRepository: WordRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel() as T
        modelClass.isAssignableFrom(MainMenuViewModel::class.java) -> MainMenuViewModel() as T
        modelClass.isAssignableFrom(AddLanguagesViewModel::class.java) -> AddLanguagesViewModel(
            languageRepository
        ) as T
        modelClass.isAssignableFrom(DictionaryViewModel::class.java) -> DictionaryViewModel(
            wordRepository
        ) as T
        else -> throw RuntimeException("Unable to create $modelClass")
    }

}