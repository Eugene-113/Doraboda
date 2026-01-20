package com.univ.doraboda.calendar.ui

import android.content.Intent
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.univ.doraboda.calendar.model.CalendarItem
import com.univ.doraboda.calendar.util.CalendarUtil
import com.univ.doraboda.R
import com.univ.doraboda.databinding.FragmentCalendarBinding
import com.univ.doraboda.calendar.db.model.Emotion
import com.univ.doraboda.calendar.db.model.Memo
import com.univ.doraboda.settings.ui.SettingsActivity
import com.univ.doraboda.app.base.BaseFragment
import com.univ.doraboda.app.dialog.DateDialogFragment
import com.univ.doraboda.calendar.viewmodel.CalendarViewModel
import com.univ.doraboda.calendar.viewmodel.CalendarViewModel.CalendarState
import com.univ.doraboda.calendar.viewmodel.CalendarViewModel.CalendarIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import kotlin.getValue

@AndroidEntryPoint
class CalendarFragment : BaseFragment<FragmentCalendarBinding>() {
    lateinit var calendarItem: Calendar //스크롤 시의 기준점이다
    //CalendarUtil을 통해 불러온 날짜 List의 가운데 position에 있는 calendarItem (즉, CalendarUtil의 인자에 들어간 Calendar의 년월 데이터를 담는다)
    var middlePositionOfItem = 50 //날짜 List의 가운데 position, 기준점 position
    var calendarAdapter: CalendarAdapter? = null
    lateinit var calendarUtil: CalendarUtil
    val viewModel: CalendarViewModel by viewModels()
    lateinit var list: ArrayList<CalendarItem> //지금 참고하는 캘린더 리스트
    var selectedCalendarItem = Calendar.getInstance() //스크롤 시마다 갱신된다
    val labelList = listOf(R.color.mainYellow, R.color.lime, R.color.pink, R.color.skyBlue)
    var previousState = CalendarState()

    override fun layoutId(): Int = R.layout.fragment_calendar

    override fun layoutInit(){
        calendarUtil = CalendarUtil()

        //지금 화면에 표시되는 년월을 다이얼로그에 전달할 목적으로 사용된다
        binding.calendarSettingsImageView.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        val calendar = Calendar.getInstance()
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0) //화면 진입 시 시간

        calendarUtil.setCalendar(calendar)
        middlePositionOfItem = calendarUtil.getMiddlePointAndSetNums()
        val calendar1 = calendarUtil.getStartDay()
        val calendar2 = calendarUtil.getEndDay()

        val manager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        calendarAdapter = CalendarAdapter(requireContext(), calendar)
        binding.calendarRecyclerView.apply {
            layoutManager = manager
            adapter = calendarAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) { //리사이클러뷰 스크롤 시,
                    super.onScrolled(recyclerView, dx, dy)
                    val lm = layoutManager as LinearLayoutManager
                    val visibleItemPosition = lm.findFirstVisibleItemPosition()
                    val thisCalendarItem = Calendar.getInstance()
                    thisCalendarItem.set(calendarItem.get(Calendar.YEAR), calendarItem.get(Calendar.MONTH), 1, 0, 0) //기준점 날짜
                    thisCalendarItem.add(Calendar.MONTH, visibleItemPosition-middlePositionOfItem) //기준점이 되는 년월에서 (리사이클러뷰 스크롤 위치에 대한) 변위를 더한다
                    selectedCalendarItem = thisCalendarItem //현재 아이템 위치 저장
                    binding.dateTextView.text = "${thisCalendarItem.get(Calendar.YEAR)}년 ${thisCalendarItem.get(Calendar.MONTH)+1}월"
                }
            })
        }
        calendarAdapter!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) { //새로운 리스트 감지
                binding.calendarRecyclerView.scrollToPosition(middlePositionOfItem)
            }
        })

        setDateAndTextView(calendar)
        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(binding.calendarRecyclerView)

        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.state.collect{
                    if(it.isError){
                        Timber.d("error in calendar")
                    }
                    if(it.isLoading){
                    } else { //memo, emotion, labelColor 바뀔때마다 같은 기간 내에서 수정
                        if(it.updateID != previousState.updateID){
                            Timber.d("collected and submit")
                            list = calendarUtil.getDays(labelList.get(it.labelColorIndex))
                            submitAdapterList(list, it.memos, it.emotions)
                            previousState = it
                        }
                    }
                }
            }
        }

        viewModel.handleIntent(CalendarIntent.LoadBetweenUserData(calendar1.timeInMillis, calendar2.timeInMillis)) //아이템배치 변경요청 (최초)

        parentFragmentManager.setFragmentResultListener("calendarResult", this){ _, bundle ->
            val dialogDate = bundle.getString("dialogDate") ?: "2001/1"
            val dateArr = dialogDate.split("/")
            val changedCalendar = Calendar.getInstance()
            changedCalendar.set(dateArr.get(0).toInt(), dateArr.get(1).toInt() - 1, 1, 0, 0, 0)
            changedCalendar.set(Calendar.MILLISECOND, 0) //현재 선택된 년월

            selectedCalendarItem = changedCalendar

            calendarUtil.setCalendar(changedCalendar)
            middlePositionOfItem = calendarUtil.getMiddlePointAndSetNums()
            val calendar3 = calendarUtil.getStartDay()
            val calendar4 = calendarUtil.getEndDay()
            viewModel.handleIntent(CalendarIntent.LoadBetweenUserData(calendar3.timeInMillis, calendar4.timeInMillis)) //아이템배치 변경요청
            setDateAndTextView(changedCalendar)
        }
        binding.dateTextView.setOnClickListener {
            DateDialogFragment().apply {
                arguments = bundleOf("fragmentDate" to "${selectedCalendarItem.get(Calendar.YEAR)}/${selectedCalendarItem.get(Calendar.MONTH)+1}")
            }.show(parentFragmentManager, "dialog")
        }
    }
    fun setDateAndTextView(calendar: Calendar){ //리사이클러뷰 아이템 갱신하면서
        //받은 날짜 데이터 기반으로 화면 상단의 년월 텍스트 설정, 받은 날짜 데이터를 기준점 데이터로 저장할 목적
        binding.dateTextView.text = "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"
        calendarItem = calendar
    }

    fun submitAdapterList(thisList: ArrayList<CalendarItem>, memos: List<Memo>?, emotions: List<Emotion>?){
            for(item in memos!!){
                val itemCalendar = Calendar.getInstance()
                itemCalendar.time = item.ID //현재 Data 아이템
                val itemIndex = thisList.indexOfFirst { it.year == itemCalendar.get(Calendar.YEAR) && it.month == itemCalendar.get(Calendar.MONTH)+1 }
                thisList[itemIndex].memoListMap.set(itemCalendar.get(Calendar.DAY_OF_MONTH), 1)
            }
            for(item in emotions!!){
                val itemCalendar = Calendar.getInstance()
                itemCalendar.time = item.ID //현재 Data 아이템
                val itemIndex = thisList.indexOfFirst { it.year == itemCalendar.get(Calendar.YEAR) && it.month == itemCalendar.get(Calendar.MONTH)+1 }
                thisList[itemIndex].emotionListMap.set(itemCalendar.get(Calendar.DAY_OF_MONTH), item.emotion.toString())
            }
            calendarAdapter!!.submitList(thisList)
    }
}