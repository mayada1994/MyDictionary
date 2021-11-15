package com.mayada1994.mydictionary_mvi.fragments

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
import com.mayada1994.mydictionary_mvi.activities.MainActivity
import com.mayada1994.mydictionary_mvi.adapters.WordsAdapter
import com.mayada1994.mydictionary_mvi.databinding.DialogAddNewWordBinding
import com.mayada1994.mydictionary_mvi.databinding.DialogDeleteWordBinding
import com.mayada1994.mydictionary_mvi.databinding.FragmentDictionaryBinding
import com.mayada1994.mydictionary_mvi.decorators.WordsDecoration
import com.mayada1994.mydictionary_mvi.di.DictionaryComponent
import com.mayada1994.mydictionary_mvi.entities.LanguageInfo
import com.mayada1994.mydictionary_mvi.entities.Word
import com.mayada1994.mydictionary_mvi.interactors.DictionaryInteractor
import com.mayada1994.mydictionary_mvi.presenters.DictionaryPresenter
import com.mayada1994.mydictionary_mvi.states.DictionaryState
import com.mayada1994.mydictionary_mvi.views.DictionaryView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*

class DictionaryFragment : Fragment(), DictionaryView {

    private lateinit var binding: FragmentDictionaryBinding

    private lateinit var presenter: DictionaryPresenter

    private lateinit var textToSpeechEngine: TextToSpeech

    private val saveButtonSubject: PublishSubject<Pair<Editable?, Editable?>> = PublishSubject.create()

    private val deleteButtonSubject: PublishSubject<Word> = PublishSubject.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = DictionaryPresenter(DictionaryInteractor(DictionaryComponent.wordRepository))
        presenter.bind(this)
    }

    override fun render(state: DictionaryState) {
        when (state) {
            is DictionaryState.DataState -> renderDataState(state.defaultLanguage, state.words, state.resId)

            is DictionaryState.ShowAddNewWordDialogState -> showAddNewWordDialog()

            is DictionaryState.LoadingState -> renderLoadingState()

            is DictionaryState.EmptyState -> renderEmptyState(state.defaultLanguage)

            is DictionaryState.CompletedState -> renderCompletedState(state.resId)

            is DictionaryState.ErrorState -> renderErrorState(state.resId)
        }
    }

    override fun displayWordsIntent(): Observable<Unit> = Observable.just(Unit)

    override fun addButtonClickIntent(): Observable<Unit> {
        return Observable.create { emitter ->
            binding.btnAdd.setOnClickListener { emitter.onNext(Unit) }
        }
    }

    override fun saveButtonClickIntent(): Observable<Pair<Editable?, Editable?>> = saveButtonSubject

    override fun deleteButtonClickIntent(): Observable<Word> = deleteButtonSubject

    private fun renderDataState(defaultLanguage: LanguageInfo, words: List<Word>, resId: Int?) {
        showPlaceholder(false)
        showProgress(false)

        setToolbar(defaultLanguage)
        setWords(words)

        resId?.let { showToast(it) }
    }

    private fun showAddNewWordDialog() {
        val dialogView = DialogAddNewWordBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext()).setView(dialogView.root).create()

        with(dialogView) {
            btnSave.setOnClickListener {
                alertDialog.dismiss()
                saveButtonSubject.onNext(fWord.text to fTranslation.text)
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
                deleteButtonSubject.onNext(word)
            }
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    private fun setToolbar(defaultLanguage: LanguageInfo) {
        showProgress(false)

        textToSpeechEngine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeechEngine.language = Locale.forLanguageTag(defaultLanguage.locale)
            }
        }

        with(binding) {
            toolbar.imgDefaultFlag.setImageResource(defaultLanguage.imageRes)
            toolbar.txtDefaultLanguage.text = getString(defaultLanguage.nameRes)
        }
    }

    private fun setWords(words: List<Word>) {
        showProgress(false)

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

    private fun renderLoadingState() {
        showProgress(true)
    }

    private fun renderEmptyState(defaultLanguage: LanguageInfo) {
        binding.wordsRecyclerView.adapter = null
        showProgress(false)
        showPlaceholder(true)
        setToolbar(defaultLanguage)
    }

    private fun renderCompletedState(resId: Int) {
        showProgress(false)
        showPlaceholder(false)
        showToast(resId)
    }

    private fun renderErrorState(resId: Int) {
        showProgress(false)
        showPlaceholder(true)
        showToast(resId)
    }

    private fun showProgress(isVisible: Boolean) {
        (requireActivity() as MainActivity).showProgress(isVisible)
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
        presenter.unbind()
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeechEngine.shutdown()
    }

}