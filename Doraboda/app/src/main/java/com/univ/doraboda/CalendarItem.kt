package com.univ.doraboda

data class CalendarItem(val year: Int, val month: Int, val days: ArrayList<DayItem>, var memoListMap: MutableMap<Int, Int>, var emotionListMap: MutableMap<Int, String>) {
}