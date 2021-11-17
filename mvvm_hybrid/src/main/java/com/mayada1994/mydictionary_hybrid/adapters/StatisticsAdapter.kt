package com.mayada1994.mydictionary_hybrid.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mayada1994.mydictionary_hybrid.databinding.ItemStatsBinding
import com.mayada1994.mydictionary_hybrid.entities.Statistics
import java.text.SimpleDateFormat
import java.util.*

class StatisticsAdapter(private val items: List<Statistics>) :
    RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        val itemBinding =
            ItemStatsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatisticsViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class StatisticsViewHolder(private val itemBinding: ItemStatsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun onBind(item: Statistics) {
            with(itemBinding) {
                txtDate.text = convertDateToString(item.timestamp)
                txtResult.text = item.result
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun convertDateToString(timestamp: Long): String {
            return SimpleDateFormat("dd/MM/yyyy (hh:mm:ss)").format(Date(timestamp))
        }

    }

}