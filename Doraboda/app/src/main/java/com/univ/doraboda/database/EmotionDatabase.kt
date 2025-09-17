package com.univ.doraboda.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.univ.doraboda.dao.EmotionDao
import com.univ.doraboda.model.Emotion
import com.univ.doraboda.util.RoomConverter

@Database(entities = [(Emotion::class)], version = 1, exportSchema = false)
@TypeConverters(RoomConverter::class)
abstract class EmotionDatabase : RoomDatabase() {
    abstract fun emotionDao(): EmotionDao
    companion object{
        private var Instance: EmotionDatabase? = null
        fun getInstance(context: Application): EmotionDatabase?{
            if(Instance ==null){
                synchronized(EmotionDatabase::class){
                    Instance = Room.databaseBuilder(context.applicationContext, EmotionDatabase::class.java, "emotionTable").build()
                }
            }
            return Instance
        }
    }
}