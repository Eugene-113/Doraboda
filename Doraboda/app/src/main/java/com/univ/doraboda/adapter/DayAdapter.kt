package com.univ.doraboda.adapter

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.univ.doraboda.DayItem
import com.univ.doraboda.R
import com.univ.doraboda.databinding.ItemDayBinding
import com.univ.doraboda.view.ReadModeActivity
import timber.log.Timber
import java.util.Calendar

class DayAdapter(val context: Context, val yearAndMonth: String, val startForResult: ActivityResultLauncher<Intent>, val memoList: MutableMap<Int, Int>, val emotionList: MutableMap<Int, String>, val calendar: Calendar) : ListAdapter<DayItem, DayAdapter.DayViewHolder>(DayDiffCallback) {
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
        return DayViewHolder(binding, context, yearAndMonth, startForResult, memoList, emotionList, calendar)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position).day)
    }

    class DayViewHolder(val binding: ItemDayBinding, val context: Context, val yearAndMonth: String, val startForResult: ActivityResultLauncher<Intent>, val memoList: MutableMap<Int, Int>, val emotionList: MutableMap<Int, String>, val calendar: Calendar) : RecyclerView.ViewHolder(binding.root){
        fun bind(day: Int){
            if (memoList.get(day) == 1) binding.dayItemLayout2.setBackgroundColor(Color.YELLOW)
            if (emotionList.get(day) != null){
                val image = when(emotionList.get(day)){
                    "normal" -> R.drawable.normal
                    "sad" -> R.drawable.sad
                    "joyful" -> R.drawable.joyful
                    "angry" -> R.drawable.angry
                    "confused" -> R.drawable.confused
                    "happy" -> R.drawable.happy
                    else -> R.drawable.icon_add
                }
                val drawable = ContextCompat.getDrawable(context, image)
                Glide.with(context).load(drawable).into(binding.dayItemImage)
            }
            if(yearAndMonth == "${calendar.get(Calendar.YEAR)}/${calendar.get(Calendar.MONTH)+1}/" && day == calendar.get(Calendar.DAY_OF_MONTH)) binding.dayItemLayout.setBackgroundColor(Color.parseColor("#FAF4C0"))
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