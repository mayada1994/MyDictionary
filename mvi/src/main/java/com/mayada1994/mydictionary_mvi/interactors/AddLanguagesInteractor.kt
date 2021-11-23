package com.mayada1994.mydictionary_mvi.interactors

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.entities.Language
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.fragments.MainFragment
import com.mayada1994.mydictionary_mvi.repositories.LanguageRepository
import com.mayada1994.mydictionary_mvi.states.AddLanguagesState
import com.mayada1994.mydictionary_mvi.utils.CacheUtils
import com.mayada1994.mydictionary_mvi.utils.LanguageUtils
import io.reactivex.Observable

class AddLanguagesInteractor(
    private val languageRepository: LanguageRepository,
    private val cacheUtils: CacheUtils
) {

    fun getLanguages(usedLanguages: List<Language>): Observable<AddLanguagesState> {
        return Observable.just(
            AddLanguagesState.DataState(LanguageUtils.getLanguages()
                .filterNot { it.locale in usedLanguages.map { it.code } }
                .map { it.toLanguageItem() })
        )
    }

    fun onSaveButtonClick(selectedLanguages: ArrayList<LanguageInfo>, initialScreen: Boolean): Observable<AddLanguagesState> {
        return if (selectedLanguages.isNullOrEmpty()) {
            Observable.just(AddLanguagesState.CompletedState(R.string.pick_languages_warning))
        } else {
            saveLanguages(selectedLanguages, initialScreen)
        }
    }

    private fun saveLanguages(
        languages: List<LanguageInfo>,
        initialScreen: Boolean
    ): Observable<AddLanguagesState> {
        return languageRepository.insertLanguages(languages.map { it.toLanguage() })
            .map {
                if (initialScreen) {
                    cacheUtils.defaultLanguage = languages[0].locale
                    AddLanguagesState.ScreenState(MainFragment::class.java)
                } else {
                    AddLanguagesState.BackPressedState
                }
            }
            .onErrorReturn { AddLanguagesState.CompletedState(R.string.general_error) }
            .toObservable()
    }

}