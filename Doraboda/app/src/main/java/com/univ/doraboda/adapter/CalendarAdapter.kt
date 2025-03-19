package com.univ.doraboda.adapter

import android.content.Context
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

class CalendarAdapter(val context: Context) : ListAdapter<CalendarItem, CalendarAdapter.DayViewHolder>(
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

    class DayViewHolder(val binding: ItemCalendarBinding, val context: Context) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: CalendarItem){
            val dayAdapter = DayAdapter(context, "${item.year}/${item.month}/")
            dayAdapter.submitList(item.days)
            binding.dayRecyclerView.apply {
                layoutManager = GridLayoutManager(context, 7)
                adapter = dayAdapter
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}