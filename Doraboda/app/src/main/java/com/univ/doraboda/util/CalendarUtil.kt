package com.univ.doraboda.util

import com.univ.doraboda.model.CalendarItem
import com.univ.doraboda.model.DayItem
import timber.log.Timber
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar

class CalendarUtil {
    var middlePoint = 0
    fun getDays(cal: Calendar): ArrayList<CalendarItem>{
        val calendarList = ArrayList<CalendarItem>()
        val d1 = LocalDate.of(2000, 1, 1) //최소 페이지
        val d2 = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, 1)
        val d3 = LocalDate.of(3000, 12, 1) //최대 페이지
        val monthsBetween1 = ChronoUnit.MONTHS.between(d1, d2).toInt() //(d2 - d1) 최소페이지: 0, 앞에서 50번째 페이지: 49
        val monthsBetween2 = ChronoUnit.MONTHS.between(d2, d3).toInt() //최대페이지: 0, 뒤에서 50번째 페이지: 49
        var startNum = 0
        var endNum = 0
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
        for(i in startNum..endNum){
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
            calendarList.add(CalendarItem(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, dayList, mutableMapOf(), mutableMapOf()))
        }
        return calendarList
    }
}