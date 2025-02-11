package com.univ.doraboda

//day가 0이면 empty type
data class DayItem (val day: Int) {
    val emotions = ArrayList<Int>()
    fun addEmotion(emotion: Int){
        emotions.add(emotion)
    }
}