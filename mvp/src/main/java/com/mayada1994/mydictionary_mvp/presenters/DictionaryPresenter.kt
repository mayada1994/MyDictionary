package com.mayada1994.mydictionary_mvp.presenters

import android.text.Editable
import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.contracts.DictionaryContract
import com.mayada1994.mydictionary_mvp.di.DictionaryComponent
import com.mayada1994.mydictionary_mvp.entities.Word
import com.mayada1994.mydictionary_mvp.models.WordDataSource
import com.mayada1994.mydictionary_mvp.utils.LanguageUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class DictionaryPresenter(
    private val viewInterface: DictionaryContract.ViewInterface,
    private val wordDataSource: WordDataSource
): DictionaryContract.PresenterInterface {

    private val compositeDisposable = CompositeDisposable()

    private var defaultLanguage: String? = null

    override fun init() {
        DictionaryComponent.cacheUtils.defaultLanguage?.let {
            defaultLanguage = it
            getWords(it)
            LanguageUtils.getLanguageByCode(it)?.let {
                viewInterface.setToolbar(it)
            }
        }
    }

    override fun onAddButtonClick() {
        viewInterface.showAddNewWordDialog()
    }

    override fun onSaveButtonClick(word: Editable?, translation: Editable?) {
        if (word.isNullOrBlank() || translation.isNullOrBlank()) {
            viewInterface.showMessage(R.string.fill_all_fields_prompt)
            return
        }
        defaultLanguage?.let { addWordToDictionary(Word(word.toString(), translation.toString(), it)) }
    }

    override fun onDeleteButtonClick(word: Word) {
        deleteWordFromDictionary(word)
    }

    private fun getWords(defaultLanguage: String) {
        viewInterface.showProgress(true)
        compositeDisposable.add(
            wordDataSource.getWordsByLanguage(defaultLanguage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { viewInterface.showProgress(false) }
                .subscribeWith(object : DisposableSingleObserver<List<Word>>() {
                    override fun onSuccess(words: List<Word>) {
                        if (words.isNotEmpty()) {
                            viewInterface.showPlaceholder(false)
                            viewInterface.setWords(words)
                        } else {
                            viewInterface.showPlaceholder(true)
                        }
                    }

                    override fun onError(e: Throwable) {
                        viewInterface.showPlaceholder(true)
                        viewInterface.showMessage(R.string.general_error)
                    }
                })
        )
    }

    private fun addWordToDictionary(word: Word) {
        viewInterface.showProgress(true)
        compositeDisposable.addAll(
            wordDataSource.insertWord(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { viewInterface.showProgress(false) }
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        viewInterface.showMessage(R.string.word_added_successfully)
                        getWords(word.language)
                    }

                    override fun onError(e: Throwable) {
                        viewInterface.showMessage(R.string.general_error)
                    }
                })
        )
    }

    private fun deleteWordFromDictionary(word: Word) {
        viewInterface.showProgress(true)
        compositeDisposable.addAll(
            wordDataSource.deleteWord(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { viewInterface.showProgress(false) }
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        viewInterface.showMessage(R.string.word_deleted_successfully)
                        defaultLanguage?.let { getWords(it) }
                    }

                    override fun onError(e: Throwable) {
                        viewInterface.showMessage(R.string.general_error)
                    }
                })
        )
    }

    override fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

}