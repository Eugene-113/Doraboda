package com.univ.doraboda

class DayItem (val day: Int) {
    val emotions = ArrayList<Int>()
    fun addEmotion(emotion: Int){
        emotions.add(emotion)
    }
}