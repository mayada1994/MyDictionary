package com.mayada1994.mydictionary_mvp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mayada1994.mydictionary_mvp.entities.LanguageInfo
import com.mayada1994.mydictionary_mvp.items.LanguageItem
import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.databinding.ItemLanguageBinding

class LanguagesAdapter(
    private val items: List<LanguageItem>,
    private val listener: OnLanguageItemClickListener
) : RecyclerView.Adapter<LanguagesAdapter.LanguagesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguagesViewHolder {
        val itemBinding =
            ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguagesViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: LanguagesViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class LanguagesViewHolder(private val itemBinding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun onBind(item: LanguageItem) {
            with(itemBinding) {
                txtLanguage.text = root.context.getString(item.nameRes)
                imgFlag.setImageResource(item.imageRes)

                root.setOnClickListener {
                    item.isSelected = !item.isSelected
                    if (item.isSelected) {
                        languageContainer.setBackgroundResource(R.drawable.bordered_background_selected)
                        txtLanguage.setTextColor(
                            ContextCompat.getColor(
                                root.context,
                                R.color.atomic_tangerine
                            )
                        )
                    } else {
                        languageContainer.setBackgroundResource(R.drawable.bordered_background)
                        txtLanguage.setTextColor(
                            ContextCompat.getColor(
                                root.context,
                                R.color.grandis
                            )
                        )
                    }
                    listener.onClick(items.filter { it.isSelected }.map { it.toLanguageInfo() })
                }
            }
        }
    }

    interface OnLanguageItemClickListener {
        fun onClick(languages: List<LanguageInfo>)
    }
}