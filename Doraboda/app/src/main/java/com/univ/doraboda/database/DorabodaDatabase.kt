package com.univ.doraboda.database

import android.app.Application
import androidx.room.*
import com.univ.doraboda.dao.EmotionDao
import com.univ.doraboda.dao.MemoDao
import com.univ.doraboda.model.Emotion
import com.univ.doraboda.model.Memo
import com.univ.doraboda.util.RoomConverter

@Database(entities = [Memo::class, Emotion::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverter::class)
abstract class DorabodaDatabase : RoomDatabase() {
    abstract fun memoDao(): MemoDao
    abstract fun emotionDao(): EmotionDao
}