package com.univ.doraboda.adapter

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
import com.univ.doraboda.model.DayItem
import com.univ.doraboda.R
import com.univ.doraboda.databinding.ItemDayBinding
import com.univ.doraboda.ui.ReadModeActivity
import java.util.Calendar

class DayAdapter(val context: Context, val yearAndMonth: String, val memoList: MutableMap<Int, Int>, val emotionList: MutableMap<Int, String>, val calendar: Calendar, val labelColor: Int) : ListAdapter<DayItem, DayAdapter.DayViewHolder>(DayDiffCallback) {
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
        return DayViewHolder(binding, context, yearAndMonth, memoList, emotionList, calendar, labelColor)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position).day)
    }

    class DayViewHolder(val binding: ItemDayBinding, val context: Context, val yearAndMonth: String, val memoList: MutableMap<Int, Int>, val emotionList: MutableMap<Int, String>, val calendar: Calendar, val labelColor: Int) : RecyclerView.ViewHolder(binding.root){
        fun bind(day: Int){
            val dateArr = yearAndMonth.split("/")
            val thisCalendar = Calendar.getInstance()
            thisCalendar.set(dateArr.get(0).toInt(), dateArr.get(1).toInt()-1, day, 0, 0, 0)
            thisCalendar.set(Calendar.MILLISECOND, 0)
            if (memoList.get(day) == 1) binding.dayItemLayout2.setBackgroundColor(ContextCompat.getColor(context, labelColor))
            if (emotionList.get(day) != null){
                val image = when(emotionList.get(day)){
                    "normal" -> R.drawable.icon_normal
                    "sad" -> R.drawable.icon_sad
                    "joyful" -> R.drawable.icon_joy
                    "angry" -> R.drawable.icon_angry
                    "confused" -> R.drawable.icon_confused
                    "happy" -> R.drawable.icon_happy
                    else -> R.drawable.icon_add
                }
                val drawable = ContextCompat.getDrawable(context, image)
                Glide.with(context).load(drawable).into(binding.dayItemImage)
            } else {
                if(calendar.timeInMillis >= thisCalendar.timeInMillis && day != 0){
                    val drawable = ContextCompat.getDrawable(context, R.drawable.icon_empty)
                    Glide.with(context).load(drawable).into(binding.dayItemImage)
                }
            }
            if(yearAndMonth == "${calendar.get(Calendar.YEAR)}/${calendar.get(Calendar.MONTH)+1}/" && day == calendar.get(Calendar.DAY_OF_MONTH)) binding.dayItemLayout.setBackgroundColor(Color.parseColor("#FAF4C0"))
            binding.dayItemText.text =
                if(day != 0){
                    if(calendar.timeInMillis >= thisCalendar.timeInMillis){
                        binding.dayItemLayout.setOnClickListener {
                            val intent = Intent(context, ReadModeActivity::class.java)
                            intent.putExtra("Date", "${yearAndMonth}${day}")
                            context.startActivity(intent)
                        }
                    }
                    day.toString()
                }
                else ""
        }
    }
}