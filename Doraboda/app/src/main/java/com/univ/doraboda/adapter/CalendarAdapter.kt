package com.univ.doraboda.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.univ.doraboda.CalendarItem
import com.univ.doraboda.DayItem
import com.univ.doraboda.databinding.ItemCalendarBinding
import com.univ.doraboda.view.CalendarFragment
import timber.log.Timber

class CalendarAdapter(val fragment: CalendarFragment) : ListAdapter<CalendarItem, CalendarAdapter.DayViewHolder>(
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

    class DayViewHolder(val binding: ItemCalendarBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(list: ArrayList<DayItem>){
            val dayAdapter = DayAdapter()
            dayAdapter.submitList(list)
            binding.dayRecyclerView.apply {
                layoutManager = GridLayoutManager(context, 7)
                adapter = dayAdapter
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item.days)
    }
}