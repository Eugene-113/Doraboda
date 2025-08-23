package com.univ.doraboda.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.univ.doraboda.DayItem
import com.univ.doraboda.databinding.ItemDayBinding
import com.univ.doraboda.view.ReadModeActivity

class DayAdapter(val context: Context, val yearAndMonth: String, val startForResult: ActivityResultLauncher<Intent>, val memoList: MutableMap<Int, Int>) : ListAdapter<DayItem, DayAdapter.DayViewHolder>(DayDiffCallback) {
    object DayDiffCallback : DiffUtil.ItemCallback<DayItem>(){
        override fun areItemsTheSame(oldItem: DayItem, newItem: DayItem): Boolean {
            return false
        }
        override fun areContentsTheSame(oldItem: DayItem, newItem: DayItem): Boolean {
            return oldItem.day == newItem.day
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DayViewHolder {
        val binding = ItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding, context, yearAndMonth, startForResult, memoList)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position).day)
    }

    class DayViewHolder(val binding: ItemDayBinding, val context: Context, val yearAndMonth: String, val startForResult: ActivityResultLauncher<Intent>, val memoList: MutableMap<Int, Int>) : RecyclerView.ViewHolder(binding.root){
        fun bind(day: Int){
            if (memoList.get(day) == 1) binding.dayItemLayout2.setBackgroundColor(Color.YELLOW)
            binding.dayItemText.text =
                if(day != 0){
                    binding.dayItemLayout.setOnClickListener {
                        val intent = Intent(context, ReadModeActivity::class.java)
                        intent.putExtra("Date", "${yearAndMonth}${day}")
                        startForResult.launch(intent)
                    }
                    day.toString()
                }
                else ""
        }
    }
}