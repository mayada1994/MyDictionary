package com.mayada1994.mydictionary_mvi.interactors

import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.di.DictionaryComponent
import com.mayada1994.mydictionary_mvi.entities.Language
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.items.DefaultLanguageItem
import com.mayada1994.mydictionary_mvi.repositories.LanguageRepository
import com.mayada1994.mydictionary_mvi.states.DefaultLanguageState
import com.mayada1994.mydictionary_mvi.utils.LanguageUtils
import io.reactivex.Observable

class DefaultLanguageInteractor(private val languageRepository: LanguageRepository) {

    private var defaultLanguage: String? = null

    private var currentLanguages: List<Language> = emptyList()

    fun getData(): Observable<DefaultLanguageState> {
        DictionaryComponent.cacheUtils.defaultLanguage?.let {
            defaultLanguage = it
            return getLanguages(LanguageUtils.getLanguageByCode(it)!!)
        }
        return Observable.just(
            DefaultLanguageState.ErrorState(
                LanguageUtils.getLanguageByCode(
                    defaultLanguage!!
                )!!, R.string.general_error
            )
        )
    }

    private fun getLanguages(defaultLanguage: LanguageInfo): Observable<DefaultLanguageState> {
        return languageRepository.getLanguages()
            .map<DefaultLanguageState> { languages ->
                currentLanguages = languages
                DefaultLanguageState.DataState(
                    defaultLanguage,
                    generateDefaultLanguageItems(languages),
                    languages.size != LanguageUtils.languagesTotal
                )
            }
            .onErrorReturn {
                DefaultLanguageState.ErrorState(
                    defaultLanguage,
                    R.string.general_error
                )
            }
            .toObservable()
    }

    private fun generateDefaultLanguageItems(languages: List<Language>): List<DefaultLanguageItem> {
        val languageItems = arrayListOf<DefaultLanguageItem>()
        languages.forEach { language ->
            LanguageUtils.getLanguageByCode(language.code)?.let { languageInfo ->
                languageItems.add(
                    languageInfo.toDefaultLanguageItem()
                        .apply { isDefault = locale == defaultLanguage })
            }
        }
        return languageItems
    }

    fun onAddButtonClick(): Observable<DefaultLanguageState> {
        return Observable.just(DefaultLanguageState.NavigateToAddLanguagesFragmentState(currentLanguages))
    }

    fun setDefaultLanguage(language: DefaultLanguageItem): Observable<DefaultLanguageState> {
        DictionaryComponent.cacheUtils.defaultLanguage = language.locale
        LanguageUtils.getLanguageByCode(language.locale)?.let {
            defaultLanguage = it.locale
            return Observable.just(DefaultLanguageState.ToolbarState(it))
        }
        return Observable.just(
            DefaultLanguageState.ErrorState(
                LanguageUtils.getLanguageByCode(
                    defaultLanguage!!
                )!!, R.string.general_error
            )
        )
    }

}