package com.univ.doraboda.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.univ.doraboda.DayItem
import com.univ.doraboda.databinding.ItemDayBinding
import com.univ.doraboda.view.ReadModeActivity
import com.univ.doraboda.view.SettingsActivity

class DayAdapter(val context: Context, val yearAndMonth: String) : ListAdapter<DayItem, DayAdapter.DayViewHolder>(DayDiffCallback) {
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
        return DayViewHolder(binding, context, yearAndMonth)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position).day)
    }

    class DayViewHolder(val binding: ItemDayBinding, val context: Context, val yearAndMonth: String) : RecyclerView.ViewHolder(binding.root){
        fun bind(day: Int){
            binding.dayItemText.text =
                if(day != 0){
                    binding.dayItemLayout.setOnClickListener {
                        val intent = Intent(context, ReadModeActivity::class.java)
                        intent.putExtra("Date", "${yearAndMonth}${day}")
                        context.startActivity(intent)
                    }
                    day.toString()
                }
                else ""
        }
    }
}