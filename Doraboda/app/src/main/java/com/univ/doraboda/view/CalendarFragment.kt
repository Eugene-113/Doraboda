package com.univ.doraboda.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.univ.doraboda.util.CalendarUtil
import com.univ.doraboda.R
import com.univ.doraboda.adapter.CalendarAdapter
import com.univ.doraboda.databinding.FragmentCalendarBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

class CalendarFragment : Fragment() {
    lateinit var calendarItem: Calendar //스크롤 시의 기준점이다
    //CalendarUtil을 통해 불러온 날짜 List의 가운데 position에 있는 calendarItem (즉, CalendarUtil의 인자에 들어간 Calendar의 년월 데이터를 담는다)

    lateinit var binding: FragmentCalendarBinding
    val middlePositionOfItem = 50 //날짜 List의 가운데 position, 기준점 position

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var selectedCalendarItem = Calendar.getInstance() //스크롤 시마다 갱신된다
        //지금 화면에 표시되는 년월을 다이얼로그에 전달할 목적으로 사용된다

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false)
        val intent = Intent(activity, SettingsActivity::class.java)
        binding.calendarSettingsImageView.setOnClickListener {
            startActivity(intent)
        }
        val calendarUtil = CalendarUtil()
        val list = calendarUtil.getDays(Calendar.getInstance())
        val manager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val calendarAdapter = CalendarAdapter(activity as Context)
        binding.calendarRecyclerView.apply {
            layoutManager = manager
            adapter = calendarAdapter
            scrollToPosition(middlePositionOfItem)
            addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) { //리사이클러뷰 스크롤 시,
                    super.onScrolled(recyclerView, dx, dy)
                    val lm = layoutManager as LinearLayoutManager
                    val visibleItemPosition = lm.findFirstVisibleItemPosition()
                    val thisCalendarItem = calendarItem.clone() as Calendar
                    thisCalendarItem.add(Calendar.MONTH, visibleItemPosition-middlePositionOfItem) //기준점이 되는 년월에서 (리사이클러뷰 스크롤 위치에 대한) 변위를 더한다
                    selectedCalendarItem = thisCalendarItem //현재 아이템 위치 저장
                    binding.dateTextView.text = "${thisCalendarItem.get(Calendar.YEAR)}년 ${thisCalendarItem.get(Calendar.MONTH)+1}월"
                }
            })
        }
        calendarAdapter.submitList(list)
        setDateTextView(Calendar.getInstance())
        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(binding.calendarRecyclerView)

        binding.dateTextView.setOnClickListener {
            val calendarDatePickerLayout = layoutInflater.inflate(R.layout.dialog_calendardatepicker, null)
            val builder = AlertDialog.Builder(context)
            builder.setView(calendarDatePickerLayout)
            val dialog = builder.create()
            val yearNumberPicker = calendarDatePickerLayout.findViewById<NumberPicker>(R.id.yearNumberPicker)
            val monthNumberPicker  = calendarDatePickerLayout.findViewById<NumberPicker>(R.id.monthNumberPicker)
            val cancelButton = calendarDatePickerLayout.findViewById<Button>(R.id.calendarDatePickerCancelButton)
            val doneButton = calendarDatePickerLayout.findViewById<Button>(R.id.calendarDatePickerDoneButton)
            yearNumberPicker.minValue = 2000
            yearNumberPicker.maxValue = 3000
            monthNumberPicker.minValue = 1
            monthNumberPicker.maxValue = 12
            yearNumberPicker.value = selectedCalendarItem.get(Calendar.YEAR)
            monthNumberPicker.value = selectedCalendarItem.get(Calendar.MONTH) + 1
            dialog.show()
            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
            doneButton.setOnClickListener {
                val changedCalendar = Calendar.getInstance()
                changedCalendar.set(Calendar.YEAR, yearNumberPicker.value)
                changedCalendar.set(Calendar.MONTH, monthNumberPicker.value - 1)
                val changedList = calendarUtil.getDays(changedCalendar)
                calendarAdapter.submitList(changedList)
                lifecycleScope.launch {
                    delay(100)
                    binding.calendarRecyclerView.scrollToPosition(middlePositionOfItem)
                }
                setDateTextView(changedCalendar)
                dialog.dismiss()
            }
        }

        return binding.root
    }
    fun setDateTextView(calendar: Calendar){ //리사이클러뷰 아이템 갱신하면서
        //받은 날짜 데이터 기반으로 화면 상단의 년월 텍스트 설정, 받은 날짜 데이터를 기준점 데이터로 저장할 목적
        binding.dateTextView.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"
        calendarItem = calendar
    }
}