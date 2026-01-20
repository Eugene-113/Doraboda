package com.univ.doraboda.calendar.db.database

import androidx.room.*
import com.univ.doraboda.calendar.db.dao.EmotionDao
import com.univ.doraboda.calendar.db.dao.MemoDao
import com.univ.doraboda.calendar.db.model.Emotion
import com.univ.doraboda.calendar.db.model.Memo
import com.univ.doraboda.calendar.db.converter.RoomConverter

@Database(entities = [Memo::class, Emotion::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverter::class)
abstract class DorabodaDatabase : RoomDatabase() {
    abstract fun memoDao(): MemoDao
    abstract fun emotionDao(): EmotionDao
}