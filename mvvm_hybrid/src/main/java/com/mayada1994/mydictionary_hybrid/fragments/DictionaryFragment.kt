package com.mayada1994.mydictionary_hybrid.fragments

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
import androidx.fragment.app.viewModels
import com.mayada1994.mydictionary_hybrid.activities.MainActivity
import com.mayada1994.mydictionary_hybrid.adapters.WordsAdapter
import com.mayada1994.mydictionary_hybrid.databinding.DialogAddNewWordBinding
import com.mayada1994.mydictionary_hybrid.databinding.DialogDeleteWordBinding
import com.mayada1994.mydictionary_hybrid.databinding.FragmentDictionaryBinding
import com.mayada1994.mydictionary_hybrid.decorators.WordsDecoration
import com.mayada1994.mydictionary_hybrid.di.DictionaryComponent
import com.mayada1994.mydictionary_hybrid.entities.LanguageInfo
import com.mayada1994.mydictionary_hybrid.entities.Word
import com.mayada1994.mydictionary_hybrid.events.BaseEvent
import com.mayada1994.mydictionary_hybrid.events.DictionaryEvent
import com.mayada1994.mydictionary_hybrid.viewmodels.DictionaryViewModel
import java.util.*

class DictionaryFragment : Fragment() {

    private lateinit var binding: FragmentDictionaryBinding

    private val viewModel by viewModels<DictionaryViewModel> { DictionaryComponent.viewModelFactory }

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

        initListeners()
        initObservers()

        viewModel.init()
    }

    private fun initListeners() {
        binding.btnAdd.setOnClickListener { viewModel.onAddButtonClick() }
    }

    private fun initObservers() {
        viewModel.event.observe(viewLifecycleOwner, { event ->
            when (event) {
                is DictionaryEvent.SetWords -> setWords(event.words)

                is DictionaryEvent.ShowAddNewWordDialog -> showAddNewWordDialog()

                is BaseEvent.SetDefaultLanguage -> setToolbar(event.defaultLanguage)

                is BaseEvent.ShowProgress -> showProgress(event.isProgressVisible)

                is BaseEvent.ShowPlaceholder -> showPlaceholder(event.isVisible)

                is BaseEvent.ShowMessage -> showMessage(event.resId)
            }
        })
    }

    private fun showAddNewWordDialog() {
        val dialogView = DialogAddNewWordBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext()).setView(dialogView.root).create()

        with(dialogView) {
            btnSave.setOnClickListener {
                alertDialog.dismiss()
                viewModel.onSaveButtonClick(fWord.text.toString(), fTranslation.text.toString())
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
                viewModel.onDeleteButtonClick(word)
            }
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    private fun setToolbar(defaultLanguage: LanguageInfo) {
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

    private fun showProgress(isVisible: Boolean) {
        (requireActivity() as MainActivity).showProgress(isVisible)
    }

    private fun showPlaceholder(isVisible: Boolean) {
        binding.txtPlaceholder.isVisible = isVisible
    }

    private fun showMessage(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        textToSpeechEngine.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
        textToSpeechEngine.shutdown()
    }

}