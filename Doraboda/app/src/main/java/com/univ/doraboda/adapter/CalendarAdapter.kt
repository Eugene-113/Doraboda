package com.univ.doraboda.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.univ.doraboda.CalendarItem
import com.univ.doraboda.databinding.ItemCalendarBinding
import timber.log.Timber
import java.util.Calendar

class CalendarAdapter(val context: Context, val startForResult: ActivityResultLauncher<Intent>, val calendar: Calendar) : ListAdapter<CalendarItem, CalendarAdapter.DayViewHolder>(
    CalendarDiffCallback
) {
    object CalendarDiffCallback : DiffUtil.ItemCallback<CalendarItem>(){
        override fun areItemsTheSame(oldItem: CalendarItem, newItem: CalendarItem): Boolean {
            return oldItem.year == newItem.year && oldItem.month == newItem.month
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: CalendarItem, newItem: CalendarItem): Boolean {
            return oldItem.memoListMap == newItem.memoListMap && oldItem.emotionListMap == newItem.emotionListMap
        }
    }

    class DayViewHolder(val binding: ItemCalendarBinding, val context: Context, val startForResult: ActivityResultLauncher<Intent>, val calendar: Calendar) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: CalendarItem){
            val dayAdapter = DayAdapter(context, "${item.year}/${item.month}/", startForResult, item.memoListMap, item.emotionListMap, calendar)
            binding.dayRecyclerView.apply {
                layoutManager = GridLayoutManager(context, 7)
                adapter = dayAdapter
            }
            dayAdapter.submitList(item.days)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding, context, startForResult, calendar)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}