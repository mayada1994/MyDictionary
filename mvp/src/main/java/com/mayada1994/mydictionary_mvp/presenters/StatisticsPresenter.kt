package com.mayada1994.mydictionary_mvp.presenters

import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.contracts.StatisticsContract
import com.mayada1994.mydictionary_mvp.entities.Statistics
import com.mayada1994.mydictionary_mvp.models.StatisticsDataSource
import com.mayada1994.mydictionary_mvp.utils.CacheUtils
import com.mayada1994.mydictionary_mvp.utils.LanguageUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class StatisticsPresenter(
    private val viewInterface: StatisticsContract.ViewInterface,
    private val statisticsDataSource: StatisticsDataSource,
    private val cacheUtils: CacheUtils
): StatisticsContract.PresenterInterface {

    private val compositeDisposable = CompositeDisposable()

    override fun init() {
        cacheUtils.defaultLanguage?.let {
            getStats(it)
            LanguageUtils.getLanguageByCode(it)?.let { viewInterface.setToolbar(it) }
        }
    }

    private fun getStats(language: String) {
        viewInterface.showProgress(true)
        compositeDisposable.add(
            statisticsDataSource.getStatisticsByLanguage(language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { viewInterface.showProgress(false) }
                .subscribeWith(object : DisposableSingleObserver<List<Statistics>>() {
                    override fun onSuccess(statistics: List<Statistics>) {
                        if (statistics.isNotEmpty()) {
                            viewInterface.showPlaceholder(false)
                            viewInterface.setStats(statistics)
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

    override fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

}