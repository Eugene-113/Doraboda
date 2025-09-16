package com.univ.doraboda.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.univ.doraboda.CalendarItem
import com.univ.doraboda.util.CalendarUtil
import com.univ.doraboda.R
import com.univ.doraboda.adapter.CalendarAdapter
import com.univ.doraboda.databinding.FragmentCalendarBinding
import com.univ.doraboda.repository.MemoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Calendar

class CalendarFragment : Fragment() {
    lateinit var calendarItem: Calendar //스크롤 시의 기준점이다
    //CalendarUtil을 통해 불러온 날짜 List의 가운데 position에 있는 calendarItem (즉, CalendarUtil의 인자에 들어간 Calendar의 년월 데이터를 담는다)

    lateinit var binding: FragmentCalendarBinding
    val middlePositionOfItem = 50 //날짜 List의 가운데 position, 기준점 position
    var calendarAdapter: CalendarAdapter? = null
    val calendarUtil = CalendarUtil()
    lateinit var application: android.app.Application

    val startForResult: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        Timber.d("startforresult")
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if(intent != null){
                val dayInfo = intent.getStringExtra("DayAndExist")
                if(dayInfo != null){
                    val dayInfos = dayInfo.split("/")
                    if(dayInfos.get(3) == "true" || dayInfos.get(4) == "true"){
                        val thisCalendar = Calendar.getInstance()
                        thisCalendar.set(dayInfos.get(0).toInt(), dayInfos.get(1).toInt()-1, dayInfos.get(2).toInt(), 0, 0, 0)
                        val newList = calendarUtil.getDays(thisCalendar)
                        val calendar1 = getStartTime(newList)
                        val calendar2 = getEndTime(newList)
                        submitAdapterList(calendar1, calendar2, newList)
                    }
                }
            }
        }
    }

    lateinit var list: ArrayList<CalendarItem>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var selectedCalendarItem = Calendar.getInstance() //스크롤 시마다 갱신된다
        //지금 화면에 표시되는 년월을 다이얼로그에 전달할 목적으로 사용된다
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val intent = Intent(activity, SettingsActivity::class.java)
        binding.calendarSettingsImageView.setOnClickListener {
            startActivity(intent)
        }

        application = requireActivity().application
        var isInit: Boolean
        val calendar = Calendar.getInstance()
        list = calendarUtil.getDays(calendar)

        val calendar1 = getStartTime(list)
        val calendar2 = getEndTime(list)

        val manager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        calendarAdapter = CalendarAdapter(activity as Context, startForResult, requireActivity())
        binding.calendarRecyclerView.apply {
            layoutManager = manager
            adapter = calendarAdapter
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

        setDateTextView(calendar)
        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(binding.calendarRecyclerView)
        isInit = true

        submitAdapterList(calendar1, calendar2, list)

        binding.dateTextView.setOnClickListener {
            if(isInit){
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
                    val calendar3 = getStartTime(changedList)
                    val calendar4 = getEndTime(changedList)
                    lifecycleScope.launch(Dispatchers.IO){
                        val dataList = MemoRepository(application).getBetween(calendar3.timeInMillis, calendar4.timeInMillis)
                        withContext(Dispatchers.Main){
                            for(item in dataList){
                                val itemCalendar = Calendar.getInstance()
                                itemCalendar.time = item.ID //현재 Data 아이템
                                val itemIndex = changedList.indexOfFirst { it.year == itemCalendar.get(Calendar.YEAR) && it.month == itemCalendar.get(Calendar.MONTH)+1 }
                                if(item.memo != null) changedList[itemIndex].memoListMap.set(itemCalendar.get(Calendar.DAY_OF_MONTH), 1)
                                if(item.emotion != null) changedList[itemIndex].emotionListMap.set(itemCalendar.get(Calendar.DAY_OF_MONTH), item.emotion.toString())
                            }
                            calendarAdapter!!.submitList(changedList)
                            delay(100)
                            binding.calendarRecyclerView.scrollToPosition(middlePositionOfItem)
                            setDateTextView(changedCalendar)
                            dialog.dismiss()
                        }
                    }
                }
            }
        }

        return binding.root
    }
    fun setDateTextView(calendar: Calendar){ //리사이클러뷰 아이템 갱신하면서
        //받은 날짜 데이터 기반으로 화면 상단의 년월 텍스트 설정, 받은 날짜 데이터를 기준점 데이터로 저장할 목적
        binding.dateTextView.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"
        calendarItem = calendar
    }

    fun getStartTime(list: ArrayList<CalendarItem>): Calendar{
        val date1 = list.get(0)
        val calendar1 = Calendar.getInstance()
        calendar1.set(date1.year, date1.month-1, 1, 0, 0, 0)
        calendar1.set(Calendar.MILLISECOND, 0)
        return calendar1
    }

    fun getEndTime(list: ArrayList<CalendarItem>): Calendar{
        val date2 = list.get(list.size-1)
        val calendar2 = Calendar.getInstance()
        calendar2.set(date2.year, date2.month-1, 32, 0, 0, 0)
        calendar2.set(Calendar.MILLISECOND, 0)
        return calendar2
    }

    fun submitAdapterList(calendar1: Calendar, calendar2: Calendar, list: ArrayList<CalendarItem>){
        lifecycleScope.launch(Dispatchers.IO){
            val dataList = MemoRepository(application).getBetween(calendar1.timeInMillis, calendar2.timeInMillis)
            withContext(Dispatchers.Main){
                for(item in dataList){
                    val itemCalendar = Calendar.getInstance()
                    itemCalendar.time = item.ID //현재 Data 아이템
                    val itemIndex = list.indexOfFirst { it.year == itemCalendar.get(Calendar.YEAR) && it.month == itemCalendar.get(Calendar.MONTH)+1 }
                    if(item.memo != null) list[itemIndex].memoListMap.set(itemCalendar.get(Calendar.DAY_OF_MONTH), 1)
                    if(item.emotion != null) list[itemIndex].emotionListMap.set(itemCalendar.get(Calendar.DAY_OF_MONTH), item.emotion.toString())
                }
                calendarAdapter!!.submitList(list)
                delay(100)
                binding.calendarRecyclerView.scrollToPosition(middlePositionOfItem)
            }
        }
    }
}