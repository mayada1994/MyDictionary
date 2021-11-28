package com.mayada1994.mydictionary_mvvm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mayada1994.mydictionary_mvvm.R
import com.mayada1994.mydictionary_mvvm.entities.LanguageInfo
import com.mayada1994.mydictionary_mvvm.entities.Word
import com.mayada1994.mydictionary_mvvm.repositories.WordRepository
import com.mayada1994.mydictionary_mvvm.utils.CacheUtils
import com.mayada1994.mydictionary_mvvm.utils.LanguageUtils
import com.mayada1994.mydictionary_mvvm.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class DictionaryViewModel(
    private val wordRepository: WordRepository,
    private val cacheUtils: CacheUtils
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _wordsList = SingleLiveEvent<List<Word>>()
    val wordsList: LiveData<List<Word>>
        get() = _wordsList

    private val _defaultLanguage = SingleLiveEvent<LanguageInfo>()
    val defaultLanguage: LiveData<LanguageInfo>
        get() = _defaultLanguage

    private val _showAddNewWordDialog = SingleLiveEvent<Boolean>()
    val showAddNewWordDialog: LiveData<Boolean>
        get() = _showAddNewWordDialog

    private val _isProgressVisible = SingleLiveEvent<Boolean>()
    val isProgressVisible: LiveData<Boolean>
        get() = _isProgressVisible

    private val _isPlaceholderVisible = SingleLiveEvent<Boolean>()
    val isPlaceholderVisible: LiveData<Boolean>
        get() = _isPlaceholderVisible

    private val _toastMessageStringResId = SingleLiveEvent<Int>()
    val toastMessageStringResId: LiveData<Int>
        get() = _toastMessageStringResId

    fun init() {
        cacheUtils.defaultLanguage?.let {
            getWords(it)
            LanguageUtils.getLanguageByCode(it)?.let {
                _defaultLanguage.postValue(it)
            }
        }
    }

    fun onAddButtonClick() {
        _showAddNewWordDialog.postValue(true)
    }

    fun onSaveButtonClick(word: String?, translation: String?) {
        if (word.isNullOrBlank() || translation.isNullOrBlank()) {
            _toastMessageStringResId.postValue(R.string.fill_all_fields_prompt)
            return
        }
        defaultLanguage.value?.let { addWordToDictionary(Word(word.toString(), translation.toString(), it.locale)) }
    }

    fun onDeleteButtonClick(word: Word) {
        deleteWordFromDictionary(word)
    }

    private fun getWords(defaultLanguage: String) {
        _isProgressVisible.postValue(true)
        compositeDisposable.add(
            wordRepository.getWordsByLanguage(defaultLanguage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { _isProgressVisible.postValue(false) }
                .subscribeWith(object : DisposableSingleObserver<List<Word>>() {
                    override fun onSuccess(words: List<Word>) {
                        if (words.isNotEmpty()) {
                            _isPlaceholderVisible.postValue(false)
                            _wordsList.postValue(words)
                        } else {
                            _isPlaceholderVisible.postValue(true)
                        }
                    }

                    override fun onError(e: Throwable) {
                        _isPlaceholderVisible.postValue(true)
                        _toastMessageStringResId.postValue(R.string.general_error)
                    }
                })
        )
    }

    private fun addWordToDictionary(word: Word) {
        _isProgressVisible.postValue(true)
        compositeDisposable.addAll(
            wordRepository.insertWord(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { _isProgressVisible.postValue(false) }
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        _toastMessageStringResId.postValue(R.string.word_added_successfully)
                        getWords(word.language)
                    }

                    override fun onError(e: Throwable) {
                        _toastMessageStringResId.postValue(R.string.general_error)
                    }
                })
        )
    }

    private fun deleteWordFromDictionary(word: Word) {
        _isProgressVisible.postValue(true)
        compositeDisposable.addAll(
            wordRepository.deleteWord(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { _isProgressVisible.postValue(false) }
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        _toastMessageStringResId.postValue(R.string.word_deleted_successfully)
                        defaultLanguage.value?.let { getWords(it.locale) }
                    }

                    override fun onError(e: Throwable) {
                        _toastMessageStringResId.postValue(R.string.general_error)
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