package com.univ.doraboda.calendar.util

import com.univ.doraboda.calendar.model.CalendarItem
import com.univ.doraboda.calendar.model.DayItem
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar

class CalendarUtil {
    private lateinit var calendar: Calendar
    private var startNum = 0
    private var endNum = 0
    fun setCalendar(cal: Calendar){
        calendar = cal
    }
    fun getMiddlePointAndSetNums(): Int{
        var middlePoint: Int
        val d1 = LocalDate.of(2000, 1, 1) //최소 페이지
        val d2 = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, 1)
        val d3 = LocalDate.of(3000, 12, 1) //최대 페이지
        val monthsBetween1 = ChronoUnit.MONTHS.between(d1, d2).toInt() //(d2 - d1) 최소페이지: 0, 앞에서 50번째 페이지: 49
        val monthsBetween2 = ChronoUnit.MONTHS.between(d2, d3).toInt() //최대페이지: 0, 뒤에서 50번째 페이지: 49
        if(monthsBetween1 < 50){
            middlePoint = monthsBetween1
            startNum = -monthsBetween1
            endNum = 50
        } else{
            middlePoint = 50
            startNum = -50
            if(monthsBetween2 < 50) {
                endNum = monthsBetween2
            } else{
                endNum = 50
            }
        }
        return middlePoint
    }
    fun getDays(labelColor: Int): ArrayList<CalendarItem>{
        val calendarList = ArrayList<CalendarItem>()
        for(i in startNum..endNum){
            val thisCalendar = Calendar.getInstance()
            thisCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + i, 1, 0, 0, 0)
            val dayList = ArrayList<DayItem>()
            for(j in 0..<thisCalendar.get(Calendar.DAY_OF_WEEK)-1){
                dayList.add(DayItem(0))
            }
            for(j in 1..thisCalendar.getActualMaximum(Calendar.DATE)){
                val dayItem = DayItem(j)
                dayList.add(dayItem)
            }
            calendarList.add(
                CalendarItem(
                    thisCalendar.get(Calendar.YEAR),
                    thisCalendar.get(Calendar.MONTH) + 1,
                    dayList,
                    mutableMapOf(),
                    mutableMapOf(),
                    labelColor
                )
            )
        }
        return calendarList
    }

    fun getStartDay(): Calendar {
        val thiscalendar = Calendar.getInstance()
        thiscalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+startNum, 1, 0, 0, 0)
        thiscalendar.set(Calendar.MILLISECOND, 0)
        return thiscalendar
    }

    fun getEndDay(): Calendar {
        val thiscalendar = Calendar.getInstance()
        thiscalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+endNum, 31, 0, 0, 0)
        thiscalendar.set(Calendar.MILLISECOND, 0)
        return thiscalendar
    }

}