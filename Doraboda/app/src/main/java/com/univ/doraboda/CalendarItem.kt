package com.univ.doraboda

//type: '0'-month, '1'-day, '2'-empty
data class CalendarItem(val type: Int, val year: Int, val month: Int, val days: ArrayList<DayItem>) {
}