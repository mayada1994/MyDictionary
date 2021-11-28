package com.mayada1994.mydictionary_hybrid.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mayada1994.mydictionary_hybrid.repositories.LanguageRepository
import com.mayada1994.mydictionary_hybrid.repositories.StatisticsRepository
import com.mayada1994.mydictionary_hybrid.repositories.WordRepository
import com.mayada1994.mydictionary_hybrid.utils.CacheUtils

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val languageRepository: LanguageRepository,
    private val statisticsRepository: StatisticsRepository,
    private val wordRepository: WordRepository,
    private val cacheUtils: CacheUtils
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(cacheUtils) as T
        modelClass.isAssignableFrom(MainMenuViewModel::class.java) -> MainMenuViewModel() as T
        modelClass.isAssignableFrom(AddLanguagesViewModel::class.java) -> AddLanguagesViewModel(
            languageRepository,
            cacheUtils
        ) as T
        modelClass.isAssignableFrom(DictionaryViewModel::class.java) -> DictionaryViewModel(
            wordRepository,
            cacheUtils
        ) as T
        modelClass.isAssignableFrom(ResultViewModel::class.java) -> ResultViewModel(cacheUtils) as T
        modelClass.isAssignableFrom(QuizViewModel::class.java) -> QuizViewModel(
            wordRepository,
            statisticsRepository,
            cacheUtils
        ) as T
        modelClass.isAssignableFrom(StatisticsViewModel::class.java) -> StatisticsViewModel(
            statisticsRepository,
            cacheUtils
        ) as T
        modelClass.isAssignableFrom(DefaultLanguageViewModel::class.java) -> DefaultLanguageViewModel(
            languageRepository,
            cacheUtils
        ) as T
        else -> throw RuntimeException("Unable to create $modelClass")
    }

}