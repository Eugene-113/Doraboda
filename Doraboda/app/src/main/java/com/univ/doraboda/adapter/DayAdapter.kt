package com.univ.doraboda.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.univ.doraboda.DayItem
import com.univ.doraboda.databinding.ItemDayBinding

class DayAdapter : ListAdapter<DayItem, DayAdapter.DayViewHolder>(DayDiffCallback) {
    object DayDiffCallback : DiffUtil.ItemCallback<DayItem>(){
        override fun areItemsTheSame(oldItem: DayItem, newItem: DayItem): Boolean {
            return oldItem.day == newItem.day
        }
        override fun areContentsTheSame(oldItem: DayItem, newItem: DayItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DayViewHolder {
        val binding = ItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position).day)
    }

    class DayViewHolder(val binding: ItemDayBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(day: Int){
            binding.dayItemText.text =
                if(day != 0) day.toString()
                else ""
        }
    }
}