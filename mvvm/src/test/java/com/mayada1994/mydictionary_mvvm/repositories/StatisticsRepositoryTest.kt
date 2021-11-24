package com.mayada1994.mydictionary_mvvm.repositories

import com.mayada1994.mydictionary_mvvm.db.dao.StatisticsDao
import com.mayada1994.mydictionary_mvvm.entities.Statistics
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StatisticsRepositoryTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val statisticsDao: StatisticsDao = mockk()

    private lateinit var statisticsRepository: StatisticsRepository

    @Before
    fun setup() {
        statisticsRepository = StatisticsRepository(statisticsDao)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    @Test
    fun `When getStatisticsByLanguage is called with some language, then should return some list of stats for that language`() {
        //Given
        val language = "en"
        val stats = listOf(
            Statistics(0, "17/20", System.currentTimeMillis(), language),
            Statistics(1, "10/20", System.currentTimeMillis(), language),
            Statistics(2, "20/20", System.currentTimeMillis(), language),
            Statistics(3, "5/20", System.currentTimeMillis(), language),
        )

        every { statisticsDao.getStatisticsByLanguage(language) } returns Single.just(stats)

        //When
        val result = statisticsRepository.getStatisticsByLanguage(language)

        //Then
        result.test().assertValue(stats)
        verify { statisticsDao.getStatisticsByLanguage(language) }
    }

    @Test
    fun `When insertStatistics is called with some stat, then should save it to DB`() {
        //Given
        val stat = Statistics(0, "17/20", System.currentTimeMillis(), "en")

        every { statisticsDao.insertStatistics(stat) } returns Completable.complete()

        //When
        val result = statisticsRepository.insertStatistics(stat)

        //Then
        result.test().assertComplete()
        verify { statisticsDao.insertStatistics(stat) }
    }

}