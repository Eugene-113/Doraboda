package com.univ.doraboda.calendar.model

data class CalendarItem(val year: Int, val month: Int, val days: ArrayList<DayItem>, var memoListMap: MutableMap<Int, Int>, var emotionListMap: MutableMap<Int, String>, var labelColor: Int) {
}