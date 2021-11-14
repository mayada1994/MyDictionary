package com.mayada1994.mydictionary_mvp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mayada1994.mydictionary_mvp.items.DefaultLanguageItem
import com.mayada1994.mydictionary_mvp.R
import com.mayada1994.mydictionary_mvp.databinding.ItemLanguageBinding

class DefaultLanguageListAdapter(
    private val items: List<DefaultLanguageItem>,
    private val listener: OnLanguageItemClickListener
) : RecyclerView.Adapter<DefaultLanguageListAdapter.DefaultLanguageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultLanguageViewHolder {
        val itemBinding =
            ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DefaultLanguageViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: DefaultLanguageViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setDefault(default: DefaultLanguageItem) {
        items.forEach { it.isDefault = false }
        items.find { it.locale == default.locale }?.isDefault = true
        notifyDataSetChanged()
    }

    inner class DefaultLanguageViewHolder(private val itemBinding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun onBind(item: DefaultLanguageItem) {
            with(itemBinding) {
                txtLanguage.text = root.context.getString(item.nameRes)
                imgFlag.setImageResource(item.imageRes)

                if (item.isDefault) {
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

                itemBinding.root.setOnClickListener {
                    if (!item.isDefault) {
                        listener.onClick(item)
                    }
                }
            }
        }
    }

    interface OnLanguageItemClickListener {
        fun onClick(language: DefaultLanguageItem)
    }
}