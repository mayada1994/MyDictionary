package com.mayada1994.mydictionary_mvvm.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.RecyclerView
import com.mayada1994.mydictionary_mvvm.items.QuestionItem
import com.mayada1994.mydictionary_mvvm.databinding.ItemQuestionBinding

class QuizAdapter(
    private val items: List<QuestionItem>
) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val itemBinding =
            ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuizViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun getItems(): List<QuestionItem> = items

    inner class QuizViewHolder(private val itemBinding: ItemQuestionBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(item: QuestionItem) {
            with(itemBinding) {
                txtWord.text = item.word.name
                btnFirstAnswer.text = item.answers[0]
                btnSecondAnswer.text = item.answers[1]
                btnThirdAnswer.text = item.answers[2]
                btnFourthAnswer.text = item.answers[3]
                groupAnswers.setOnCheckedChangeListener { _, resId ->
                    item.selectedAnswer = root.findViewById<AppCompatRadioButton>(resId).text.toString()
                }
            }
        }
    }

}