package com.univ.doraboda.repository

import android.app.Application
import com.univ.doraboda.dao.EmotionDao
import com.univ.doraboda.database.EmotionDatabase
import com.univ.doraboda.model.Emotion
import java.util.Date

class EmotionRepository (application: Application) {
    val db = EmotionDatabase.getInstance(application)!!
    val dao: EmotionDao = db.emotionDao()

    fun insertEmotion(emotion: Emotion){
        dao.insertEmotion(emotion)
    }

    fun updateEmotion(id: Date, emotion: String){
        dao.updateEmotion(id, emotion)
    }

    fun deleteEmotion(date: Date){
        dao.deleteEmotion(date)
    }

    fun getEmotion(id: Date): Emotion {
        return dao.getEmotion(id)
    }

    fun getBetween(date1: Long, date2: Long): List<Emotion>{
        return dao.getBetweenEmotion(date1, date2)
    }
}
