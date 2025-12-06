package com.univ.doraboda.repository

import com.univ.doraboda.dao.MemoDao
import com.univ.doraboda.model.Memo
import java.util.Date
import javax.inject.Inject

class MemoRepository @Inject constructor(private val dao: MemoDao){

    fun insertMemo(memo: Memo){
        dao.insertMemo(memo)
    }

    fun updateMemo(id: Date, memo: String){
        dao.updateMemo(id, memo)
    }

    fun deleteMemo(date: Date){
        dao.deleteData(date)
    }

    fun getMemo(id: Date): Memo{
        return dao.getMemo(id)
    }

    fun getBetween(date1: Long, date2: Long): List<Memo>{
        return dao.getBetweenMemo(date1, date2)
    }
}