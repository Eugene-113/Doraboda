package com.univ.doraboda.repository

import com.univ.doraboda.dao.EmotionDao
import com.univ.doraboda.model.Emotion
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class EmotionRepository @Inject constructor(private val dao: EmotionDao){

    fun insertEmotion(emotion: Emotion){
        dao.insertEmotion(emotion)
    }

    fun updateEmotion(id: Date, emotion: String){
        dao.updateEmotion(id, emotion)
    }

    fun deleteEmotion(date: Date){
        dao.deleteEmotion(date)
    }

    fun getEmotion(id: Date): Flow<Emotion?> {
        return dao.getEmotion(id)
    }

    fun getBetween(date1: Long, date2: Long): Flow<List<Emotion>>{
        return dao.getBetweenEmotion(date1, date2)
    }
}
