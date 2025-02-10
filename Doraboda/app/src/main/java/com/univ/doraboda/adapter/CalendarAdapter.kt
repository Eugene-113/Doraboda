package com.univ.doraboda.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.univ.doraboda.CalendarItem
import com.univ.doraboda.databinding.ItemCalendarBinding

class CalendarAdapter : ListAdapter<CalendarItem, CalendarAdapter.CalendarViewHolder>(
    CalendarDiffCallback
) {

    object CalendarDiffCallback : DiffUtil.ItemCallback<CalendarItem>(){
        override fun areItemsTheSame(oldItem: CalendarItem, newItem: CalendarItem): Boolean {
            return oldItem.year == newItem.year && oldItem.month == newItem.month
        }
        override fun areContentsTheSame(oldItem: CalendarItem, newItem: CalendarItem): Boolean {
            return oldItem == newItem
        }
    }

    class CalendarViewHolder(val binding: ItemCalendarBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(){
            binding.dayRecyclerView.apply {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind()
    }
}