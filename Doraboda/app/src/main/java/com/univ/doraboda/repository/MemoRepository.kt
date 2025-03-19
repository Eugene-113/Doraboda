package com.univ.doraboda.repository

import android.app.Application
import com.univ.doraboda.dao.MemoDao
import com.univ.doraboda.database.MemoDatabase
import com.univ.doraboda.model.Memo

class MemoRepository (application: Application) {
    val db = MemoDatabase.getInstance(application)!!
    val dao: MemoDao = db.memoDao()

    fun insert(memo: Memo){
        dao.insertMemo(memo)
    }

    fun delete(id: String){
        dao.deleteMemo(id)
    }

    fun get(id: String): Memo{
        return dao.getMemo(id)
    }
}