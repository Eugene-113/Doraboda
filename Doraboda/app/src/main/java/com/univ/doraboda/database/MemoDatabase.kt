package com.univ.doraboda.database

import android.app.Application
import androidx.room.*
import com.univ.doraboda.dao.MemoDao
import com.univ.doraboda.model.Memo

@Database(entities = [(Memo::class)], version = 1, exportSchema = false)
abstract class MemoDatabase : RoomDatabase() {
    abstract fun memoDao(): MemoDao
    companion object{
        private var Instance: MemoDatabase? = null
        fun getInstance(context: Application): MemoDatabase?{
            if(Instance ==null){
                synchronized(MemoDatabase::class){
                    Instance = Room.databaseBuilder(context.applicationContext, MemoDatabase::class.java, "memoTable").build()
                }
            }
            return Instance
        }
    }
}