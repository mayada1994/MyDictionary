package com.mayada1994.mydictionary_mvi.interactors

import android.text.Editable
import androidx.annotation.StringRes
import com.mayada1994.mydictionary_mvi.R
import com.mayada1994.mydictionary_mvi.di.DictionaryComponent
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.entities.Word
import com.mayada1994.mydictionary_mvi.repositories.WordRepository
import com.mayada1994.mydictionary_mvi.states.DictionaryState
import com.mayada1994.mydictionary_mvi.utils.LanguageUtils
import io.reactivex.Observable

class DictionaryInteractor(private val wordRepository: WordRepository) {

    private var defaultLanguage: String? = null

    fun getData(): Observable<DictionaryState> {
        DictionaryComponent.cacheUtils.defaultLanguage?.let {
            defaultLanguage = it
            LanguageUtils.getLanguageByCode(it)?.let {
                return getWords(it)
            }
        }
        return Observable.just(DictionaryState.ErrorState(R.string.general_error))
    }

    private fun getWords(defaultLanguage: LanguageInfo, @StringRes resId: Int? = null): Observable<DictionaryState> {
        return wordRepository.getWordsByLanguage(defaultLanguage.locale)
            .map { words ->
                if (words.isNotEmpty()) {
                    DictionaryState.DataState(defaultLanguage, words, resId)
                } else {
                    DictionaryState.EmptyState(defaultLanguage)
                }
            }
            .onErrorReturn { DictionaryState.ErrorState(R.string.general_error) }
            .toObservable()
    }

    fun onAddButtonClick(): Observable<DictionaryState> {
        return Observable.just(DictionaryState.ShowAddNewWordDialogState)
    }

    fun onSaveButtonClick(word: Editable?, translation: Editable?): Observable<DictionaryState> {
        if (word.isNullOrBlank() || translation.isNullOrBlank()) {
            return Observable.just(DictionaryState.CompletedState(R.string.fill_all_fields_prompt))
        }
        defaultLanguage?.let { return addWordToDictionary(Word(word.toString(), translation.toString(), it)) }

        return Observable.just(DictionaryState.ErrorState(R.string.general_error))
    }

    private fun addWordToDictionary(word: Word): Observable<DictionaryState> {
        return wordRepository.insertWord(word)
            .map {
                LanguageUtils.getLanguageByCode(word.language)?.let { languageInfo ->
                    getWords(
                        languageInfo,
                        R.string.word_added_successfully
                    ).blockingSingle()
                } ?: run {
                    DictionaryState.CompletedState(R.string.word_added_successfully)
                }
            }
            .onErrorReturn { DictionaryState.CompletedState(R.string.general_error) }
            .toObservable()
    }

    fun deleteWordFromDictionary(word: Word): Observable<DictionaryState> {
        return wordRepository.deleteWord(word)
            .map {
                LanguageUtils.getLanguageByCode(word.language)
                    ?.let { languageInfo ->
                        getWords(
                            languageInfo,
                            R.string.word_deleted_successfully
                        ).blockingSingle()
                    } ?: run {
                    DictionaryState.CompletedState(R.string.word_deleted_successfully)
                }
            }
            .onErrorReturn { DictionaryState.CompletedState(R.string.general_error) }
            .toObservable()
    }

}