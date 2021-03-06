package com.mayada1994.mydictionary_hybrid.viewmodels

import com.mayada1994.mydictionary_hybrid.R
import com.mayada1994.mydictionary_hybrid.entities.Statistics
import com.mayada1994.mydictionary_hybrid.events.BaseEvent
import com.mayada1994.mydictionary_hybrid.events.StatisticsEvent
import com.mayada1994.mydictionary_hybrid.repositories.StatisticsRepository
import com.mayada1994.mydictionary_hybrid.utils.CacheUtils
import com.mayada1994.mydictionary_hybrid.utils.LanguageUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class StatisticsViewModel(
    private val statisticsRepository: StatisticsRepository,
    private val cacheUtils: CacheUtils
) : BaseViewModel() {

    private val compositeDisposable = CompositeDisposable()

    fun init() {
        cacheUtils.defaultLanguage?.let {
            getStats(it)
            LanguageUtils.getLanguageByCode(it)?.let { setEvent(BaseEvent.SetDefaultLanguage(it)) }
        }
    }

    private fun getStats(language: String) {
        setEvent(BaseEvent.ShowProgress(true))
        compositeDisposable.add(
            statisticsRepository.getStatisticsByLanguage(language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { setEvent(BaseEvent.ShowProgress(false)) }
                .subscribeWith(object : DisposableSingleObserver<List<Statistics>>() {
                    override fun onSuccess(statistics: List<Statistics>) {
                        if (statistics.isNotEmpty()) {
                            setEvent(BaseEvent.ShowPlaceholder(false))
                            setEvent(StatisticsEvent.SetStats(statistics))
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

    fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

}