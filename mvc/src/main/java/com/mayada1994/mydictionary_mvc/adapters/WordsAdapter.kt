package com.mayada1994.mydictionary_mvc.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mayada1994.mydictionary_mvc.databinding.ItemWordBinding
import com.mayada1994.mydictionary_mvc.entities.Word

class WordsAdapter(
    private val items: List<Word>,
    private val listener: OnWordItemClickListener
) : RecyclerView.Adapter<WordsAdapter.WordsViewHolder>() {

    init {
        listener.onWordItemClick(items[0].name, true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordsViewHolder {
        val itemBinding =
            ItemWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordsViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: WordsViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class WordsViewHolder(private val itemBinding: ItemWordBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun onBind(item: Word) {
            with(itemBinding) {
                txtWord.text = item.name
                txtTranslation.text = item.translation
                btnSpeech.setOnClickListener {
                    listener.onWordItemClick(item.name)
                }
                root.setOnLongClickListener {
                    listener.onWordItemLongClick(item)
                    return@setOnLongClickListener true
                }
            }
        }
    }

    interface OnWordItemClickListener {
        fun onWordItemClick(word: String, initial: Boolean = false)
        fun onWordItemLongClick(word: Word)
    }

}