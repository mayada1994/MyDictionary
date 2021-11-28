package com.mayada1994.mydictionary_hybrid.viewmodels

import com.mayada1994.mydictionary_hybrid.R
import com.mayada1994.mydictionary_hybrid.entities.Word
import com.mayada1994.mydictionary_hybrid.events.BaseEvent
import com.mayada1994.mydictionary_hybrid.events.DictionaryEvent
import com.mayada1994.mydictionary_hybrid.repositories.WordRepository
import com.mayada1994.mydictionary_hybrid.utils.CacheUtils
import com.mayada1994.mydictionary_hybrid.utils.LanguageUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class DictionaryViewModel(
    private val wordRepository: WordRepository,
    private val cacheUtils: CacheUtils
) : BaseViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private var defaultLanguage: String? = null

    fun init() {
        cacheUtils.defaultLanguage?.let {
            defaultLanguage = it
            getWords(it)
            LanguageUtils.getLanguageByCode(it)?.let {
                setEvent(BaseEvent.SetDefaultLanguage(it))
            }
        }
    }

    fun onAddButtonClick() {
        setEvent(DictionaryEvent.ShowAddNewWordDialog)
    }

    fun onSaveButtonClick(word: String?, translation: String?) {
        if (word.isNullOrBlank() || translation.isNullOrBlank()) {
            setEvent(BaseEvent.ShowMessage(R.string.fill_all_fields_prompt))
            return
        }
        defaultLanguage?.let { addWordToDictionary(Word(word.toString(), translation.toString(), it)) }
    }

    fun onDeleteButtonClick(word: Word) {
        deleteWordFromDictionary(word)
    }

    private fun getWords(defaultLanguage: String) {
        setEvent(BaseEvent.ShowProgress(true))
        compositeDisposable.add(
            wordRepository.getWordsByLanguage(defaultLanguage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { setEvent(BaseEvent.ShowProgress(false)) }
                .subscribeWith(object : DisposableSingleObserver<List<Word>>() {
                    override fun onSuccess(words: List<Word>) {
                        if (words.isNotEmpty()) {
                            setEvent(BaseEvent.ShowPlaceholder(false))
                            setEvent(DictionaryEvent.SetWords(words))
                        } else {
                            setEvent(BaseEvent.ShowPlaceholder(true))
                        }
                    }

                    override fun onError(e: Throwable) {
                        setEvent(BaseEvent.ShowPlaceholder(true))
                        setEvent(BaseEvent.ShowMessage(R.string.general_error))
                    }
                })
        )
    }

    private fun addWordToDictionary(word: Word) {
        setEvent(BaseEvent.ShowProgress(true))
        compositeDisposable.addAll(
            wordRepository.insertWord(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { setEvent(BaseEvent.ShowProgress(false)) }
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        setEvent(BaseEvent.ShowMessage(R.string.word_added_successfully))
                        getWords(word.language)
                    }

                    override fun onError(e: Throwable) {
                        setEvent(BaseEvent.ShowMessage(R.string.general_error))
                    }
                })
        )
    }

    private fun deleteWordFromDictionary(word: Word) {
        setEvent(BaseEvent.ShowProgress(true))
        compositeDisposable.addAll(
            wordRepository.deleteWord(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { setEvent(BaseEvent.ShowProgress(false)) }
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        setEvent(BaseEvent.ShowMessage(R.string.word_deleted_successfully))
                        defaultLanguage?.let { getWords(it) }
                    }

                    override fun onError(e: Throwable) {
                        setEvent(BaseEvent.ShowMessage(R.string.general_error))
                    }
                })
        )
    }

    fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

}