package com.univ.doraboda.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.univ.doraboda.model.Emotion
import java.util.Date

@Dao
interface EmotionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEmotion(emotion: Emotion)

    @Query("SELECT * FROM emotionTable WHERE id = :id")
    fun getEmotion(id: Date): Emotion

    @Query("UPDATE emotionTable SET emotion = :emotion WHERE id = :id")
    fun updateEmotion(id: Date, emotion: String)

    @Query("DELETE FROM emotionTable WHERE id = :id")
    fun deleteEmotion(id: Date)

    @Query("SELECT * FROM emotionTable WHERE id BETWEEN :date1 AND :date2")
    fun getBetweenEmotion(date1: Long, date2: Long): List<Emotion>
}