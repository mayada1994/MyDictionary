package com.mayada1994.mydictionary_mvc.fragments

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.mayada1994.mydictionary_mvc.R
import com.mayada1994.mydictionary_mvc.activities.MainActivity
import com.mayada1994.mydictionary_mvc.adapters.WordsAdapter
import com.mayada1994.mydictionary_mvc.databinding.DialogAddNewWordBinding
import com.mayada1994.mydictionary_mvc.databinding.DialogDeleteWordBinding
import com.mayada1994.mydictionary_mvc.databinding.FragmentDictionaryBinding
import com.mayada1994.mydictionary_mvc.decorators.WordsDecoration
import com.mayada1994.mydictionary_mvc.di.DictionaryComponent
import com.mayada1994.mydictionary_mvc.entities.Word
import com.mayada1994.mydictionary_mvc.models.WordDataSource
import com.mayada1994.mydictionary_mvc.utils.LanguageUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class DictionaryFragment : Fragment() {

    private lateinit var binding: FragmentDictionaryBinding

    private val compositeDisposable = CompositeDisposable()

    private lateinit var wordDataSource: WordDataSource

    private var defaultLanguage: String? = null

    private val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                defaultLanguage?.let {
                    textToSpeechEngine.language = Locale.forLanguageTag(it)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wordDataSource = DictionaryComponent.wordDataSource

        initListeners()
        setToolbar()
    }

    private fun initListeners() {
        binding.btnAdd.setOnClickListener { showAddNewWordDialog() }
    }

    private fun showAddNewWordDialog() {
        val dialogView = DialogAddNewWordBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext()).setView(dialogView.root).create()

        with(dialogView) {
            btnSave.setOnClickListener {
                alertDialog.dismiss()
                addWordToDictionary(fWord.text, fTranslation.text)
            }
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    private fun showDeleteWordDialog(word: Word) {
        val dialogView = DialogDeleteWordBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext()).setView(dialogView.root).create()

        with(dialogView) {
            btnDelete.setOnClickListener {
                alertDialog.dismiss()
                deleteWordFromDictionary(word)
            }
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    private fun addWordToDictionary(word: Editable?, translation: Editable?) {
        if (word.isNullOrBlank() || translation.isNullOrBlank()) {
            showToast(R.string.fill_all_fields_prompt)
            return
        }
        defaultLanguage?.let {
            (requireActivity() as MainActivity).showProgress(true)
            compositeDisposable.addAll(
                wordDataSource.insertWord(Word(word.toString(), translation.toString(), it))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally { (requireActivity() as MainActivity).showProgress(false) }
                    .subscribeWith(object : DisposableCompletableObserver() {
                        override fun onComplete() {
                            showToast(R.string.word_added_successfully)
                            getWords(it)
                        }

                        override fun onError(e: Throwable) {
                            showToast(R.string.general_error)
                        }
                    })
            )
        }

    }

    private fun deleteWordFromDictionary(word: Word) {
        (requireActivity() as MainActivity).showProgress(true)
        compositeDisposable.addAll(
            wordDataSource.deleteWord(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { (requireActivity() as MainActivity).showProgress(false) }
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        showToast(R.string.word_deleted_successfully)
                        defaultLanguage?.let { getWords(it) }
                    }

                    override fun onError(e: Throwable) {
                        showToast(R.string.general_error)
                    }
                })
        )
    }

    private fun setToolbar() {
        DictionaryComponent.cacheUtils.defaultLanguage?.let {
            defaultLanguage = it
            getWords(it)
            LanguageUtils.getLanguageByCode(it)?.let {
                with(binding) {
                    toolbar.imgDefaultFlag.setImageResource(it.imageRes)
                    toolbar.txtDefaultLanguage.text = getString(it.nameRes)
                }
            }
        }
    }

    private fun getWords(defaultLanguage: String) {
        (requireActivity() as MainActivity).showProgress(true)
        compositeDisposable.add(
            wordDataSource.getWordsByLanguage(defaultLanguage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { (requireActivity() as MainActivity).showProgress(false) }
                .subscribeWith(object : DisposableSingleObserver<List<Word>>() {
                    override fun onSuccess(words: List<Word>) {
                        if (words.isNotEmpty()) {
                            showPlaceholder(false)
                            setWords(words)
                        } else {
                            showPlaceholder(true)
                        }
                    }

                    override fun onError(e: Throwable) {
                        showPlaceholder(true)
                        showToast(R.string.general_error)
                    }
                })
        )
    }

    private fun setWords(words: List<Word>) {
        binding.wordsRecyclerView.adapter =
            WordsAdapter(words, object : WordsAdapter.OnWordItemClickListener {
                override fun onWordItemClick(word: String, initial: Boolean) {
                    textToSpeechEngine.speak(
                        word,
                        TextToSpeech.QUEUE_FLUSH,
                        Bundle().apply { putFloat(KEY_PARAM_VOLUME, if (initial) 0.0f else 1.0f) },
                        "tts1"
                    )
                }

                override fun onWordItemLongClick(word: Word) {
                    showDeleteWordDialog(word)
                }
            })
        binding.wordsRecyclerView.addItemDecoration(WordsDecoration(requireContext()))
    }

    private fun showPlaceholder(isVisible: Boolean) {
        binding.txtPlaceholder.isVisible = isVisible
    }

    private fun showToast(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        textToSpeechEngine.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
        textToSpeechEngine.shutdown()
    }

}