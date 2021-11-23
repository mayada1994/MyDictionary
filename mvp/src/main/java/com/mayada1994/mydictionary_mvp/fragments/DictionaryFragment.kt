package com.mayada1994.mydictionary_mvp.fragments

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.mayada1994.mydictionary_mvp.activities.MainActivity
import com.mayada1994.mydictionary_mvp.adapters.WordsAdapter
import com.mayada1994.mydictionary_mvp.contracts.DictionaryContract
import com.mayada1994.mydictionary_mvp.databinding.DialogAddNewWordBinding
import com.mayada1994.mydictionary_mvp.databinding.DialogDeleteWordBinding
import com.mayada1994.mydictionary_mvp.databinding.FragmentDictionaryBinding
import com.mayada1994.mydictionary_mvp.decorators.WordsDecoration
import com.mayada1994.mydictionary_mvp.di.DictionaryComponent
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.entities.Word
import com.mayada1994.mydictionary_mvp.presenters.DictionaryPresenter
import java.util.*

class DictionaryFragment : Fragment(), DictionaryContract.ViewInterface {

    private lateinit var binding: FragmentDictionaryBinding

    private lateinit var presenter: DictionaryPresenter

    private lateinit var textToSpeechEngine: TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = DictionaryPresenter(this, DictionaryComponent.wordDataSource, DictionaryComponent.cacheUtils)
        presenter.init()

        initListeners()
    }

    private fun initListeners() {
        binding.btnAdd.setOnClickListener { presenter.onAddButtonClick() }
    }

    override fun showAddNewWordDialog() {
        val dialogView = DialogAddNewWordBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext()).setView(dialogView.root).create()

        with(dialogView) {
            btnSave.setOnClickListener {
                alertDialog.dismiss()
                presenter.onSaveButtonClick(fWord.text.toString(), fTranslation.text.toString())
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
                presenter.onDeleteButtonClick(word)
            }
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    override fun setToolbar(defaultLanguage: LanguageInfo) {
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

    override fun setWords(words: List<Word>) {
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

    override fun showProgress(isVisible: Boolean) {
        (requireActivity() as MainActivity).showProgress(isVisible)
    }

    override fun showPlaceholder(isVisible: Boolean) {
        binding.txtPlaceholder.isVisible = isVisible
    }

    override fun showMessage(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        textToSpeechEngine.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
        textToSpeechEngine.shutdown()
    }

}