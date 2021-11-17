package com.mayada1994.mydictionary_mvvm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mayada1994.mydictionary_mvvm.R
import com.mayada1994.mydictionary_mvvm.di.DictionaryComponent
import com.mayada1994.mydictionary_mvvm.entities.LanguageInfo
import com.mayada1994.mydictionary_mvvm.entities.Statistics
import com.mayada1994.mydictionary_mvvm.repositories.StatisticsRepository
import com.mayada1994.mydictionary_mvvm.utils.LanguageUtils
import com.mayada1994.mydictionary_mvvm.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class StatisticsViewModel(private val statisticsRepository: StatisticsRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _statsList = SingleLiveEvent<List<Statistics>>()
    val statsList: LiveData<List<Statistics>>
        get() = _statsList

    private val _defaultLanguage = SingleLiveEvent<LanguageInfo>()
    val defaultLanguage: LiveData<LanguageInfo>
        get() = _defaultLanguage

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
        DictionaryComponent.cacheUtils.defaultLanguage?.let {
            getStats(it)
            LanguageUtils.getLanguageByCode(it)?.let { _defaultLanguage.postValue(it) }
        }
    }

    private fun getStats(language: String) {
        _isProgressVisible.postValue(true)
        compositeDisposable.add(
            statisticsRepository.getStatisticsByLanguage(language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { _isProgressVisible.postValue(false) }
                .subscribeWith(object : DisposableSingleObserver<List<Statistics>>() {
                    override fun onSuccess(statistics: List<Statistics>) {
                        if (statistics.isNotEmpty()) {
                            _isPlaceholderVisible.postValue(false)
                            _statsList.postValue(statistics)
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

    fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

}