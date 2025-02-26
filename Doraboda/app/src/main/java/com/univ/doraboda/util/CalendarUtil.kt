package com.univ.doraboda.util

import com.univ.doraboda.CalendarItem
import com.univ.doraboda.DayItem
import java.util.Calendar

class CalendarUtil {
    fun getDays(cal: Calendar): ArrayList<CalendarItem>{
        val calendarList = ArrayList<CalendarItem>()
        for(i in -50..50){
            val calendar = Calendar.getInstance()
            calendar.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + i, 1, 0, 0, 0)
            val dayList = ArrayList<DayItem>()
            for(j in 0..<calendar.get(Calendar.DAY_OF_WEEK)-1){
                dayList.add(DayItem(0))
            }
            for(j in 1..calendar.getActualMaximum(Calendar.DATE)){
                val dayItem = DayItem(j)
                dayList.add(dayItem)
            }
            calendarList.add(CalendarItem(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, dayList))
        }
        return calendarList
    }
}