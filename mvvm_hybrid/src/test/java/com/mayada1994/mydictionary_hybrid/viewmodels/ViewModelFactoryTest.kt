package com.mayada1994.mydictionary_hybrid.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import com.mayada1994.mydictionary_hybrid.repositories.LanguageRepository
import com.mayada1994.mydictionary_hybrid.repositories.StatisticsRepository
import com.mayada1994.mydictionary_hybrid.repositories.WordRepository
import com.mayada1994.mydictionary_hybrid.utils.CacheUtils
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class ViewModelFactoryTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val languageRepository: LanguageRepository = mockk()

    private val statisticsRepository: StatisticsRepository = mockk()

    private val wordRepository: WordRepository = mockk()

    private val cacheUtils: CacheUtils = mockk()

    private lateinit var viewModelFactory: ViewModelFactory

    @Before
    fun setup() {
        viewModelFactory = ViewModelFactory(languageRepository, statisticsRepository, wordRepository, cacheUtils)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    @Test
    fun `When create in viewModelFactory called with AddLanguagesViewModel as class, then should return instance of AddLanguagesViewModel`() {
        //Given
        val viewModel = AddLanguagesViewModel(languageRepository, cacheUtils)

        //When
        val result = viewModelFactory.create(AddLanguagesViewModel::class.java)

        //Then
        assertEquals(viewModel.javaClass, result.javaClass)
    }

    @Test
    fun `When create in viewModelFactory called with DefaultLanguageViewModel as class, then should return instance of DefaultLanguageViewModel`() {
        //Given
        val viewModel = DefaultLanguageViewModel(languageRepository, cacheUtils)

        //When
        val result = viewModelFactory.create(DefaultLanguageViewModel::class.java)

        //Then
        assertEquals(viewModel.javaClass, result.javaClass)
    }

    @Test
    fun `When create in viewModelFactory called with DictionaryViewModel as class, then should return instance of DictionaryViewModel`() {
        //Given
        val viewModel = DictionaryViewModel(wordRepository, cacheUtils)

        //When
        val result = viewModelFactory.create(DictionaryViewModel::class.java)

        //Then
        assertEquals(viewModel.javaClass, result.javaClass)
    }

    @Test
    fun `When create in viewModelFactory called with MainMenuViewModel as class, then should return instance of MainMenuViewModel`() {
        //Given
        val viewModel = MainMenuViewModel()

        //When
        val result = viewModelFactory.create(MainMenuViewModel::class.java)

        //Then
        assertEquals(viewModel.javaClass, result.javaClass)
    }

    @Test
    fun `When create in viewModelFactory called with MainViewModel as class, then should return instance of MainViewModel`() {
        //Given
        val viewModel = MainViewModel(cacheUtils)

        //When
        val result = viewModelFactory.create(MainViewModel::class.java)

        //Then
        assertEquals(viewModel.javaClass, result.javaClass)
    }

    @Test
    fun `When create in viewModelFactory called with QuizViewModel as class, then should return instance of QuizViewModel`() {
        //Given
        val viewModel = QuizViewModel(wordRepository, statisticsRepository, cacheUtils)

        //When
        val result = viewModelFactory.create(QuizViewModel::class.java)

        //Then
        assertEquals(viewModel.javaClass, result.javaClass)
    }

    @Test
    fun `When create in viewModelFactory called with ResultViewModel as class, then should return instance of ResultViewModel`() {
        //Given
        val viewModel = ResultViewModel(cacheUtils)

        //When
        val result = viewModelFactory.create(ResultViewModel::class.java)

        //Then
        assertEquals(viewModel.javaClass, result.javaClass)
    }

    @Test
    fun `When create in viewModelFactory called with StatisticsViewModel as class, then should return instance of StatisticsViewModel`() {
        //Given
        val viewModel = StatisticsViewModel(statisticsRepository, cacheUtils)

        //When
        val result = viewModelFactory.create(StatisticsViewModel::class.java)

        //Then
        assertEquals(viewModel.javaClass, result.javaClass)
    }

    @Test
    fun `When create in viewModelFactory called with other class, then should throw RuntimeException`() {
        //Given
        class TestViewModel: ViewModel()

        val testException = RuntimeException("Unable to create ${TestViewModel::class.java}")

        try {
            //When
            val result = viewModelFactory.create(TestViewModel::class.java)

            //Then
            assertEquals(testException.javaClass, result.javaClass)
        } catch (e: Exception) {
        }
    }

}