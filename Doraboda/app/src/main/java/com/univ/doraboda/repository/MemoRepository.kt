package com.univ.doraboda.repository

import android.app.Application
import com.univ.doraboda.dao.MemoDao
import com.univ.doraboda.database.MemoDatabase
import com.univ.doraboda.model.Memo
import java.util.Date

class MemoRepository (application: Application) {
    val db = MemoDatabase.getInstance(application)!!
    val dao: MemoDao = db.memoDao()

    fun insertData(memo: Memo){
        dao.insertMemo(memo)
    }

    fun updateEmotion(id: Date, emotion: String?){
        dao.updateEmotion(id, emotion)
    }

    fun updateMemo(id: Date, memo: String?){
        dao.updateMemo(id, memo)
    }

    fun deleteData(date: Date){
        dao.deleteData(date)
    }

    fun get(id: Date): Memo{
        return dao.getMemo(id)
    }

    fun getBetween(date1: Long, date2: Long): List<Memo>{
        return dao.getBetweenMemo(date1, date2)
    }
}