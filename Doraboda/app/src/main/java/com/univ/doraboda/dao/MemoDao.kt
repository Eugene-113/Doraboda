package com.univ.doraboda.dao

import androidx.room.*
import com.univ.doraboda.model.Memo
import java.util.Date

@Dao
interface MemoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMemo(memo: Memo)

    @Query("SELECT * FROM memoTable WHERE id = :id")
    fun getMemo(id: Date): Memo

    @Query("UPDATE memoTable SET memo = :memo WHERE id = :id")
    fun updateMemo(id: Date, memo: String?)

    @Query("UPDATE memoTable SET emotion = :emotion WHERE id = :id")
    fun updateEmotion(id: Date, emotion: String?)

    @Query("DELETE FROM memoTable WHERE id = :id")
    fun deleteData(id: Date)

    @Query("SELECT * FROM memoTable WHERE id BETWEEN :date1 AND :date2")
    fun getBetweenMemo(date1: Long, date2: Long): List<Memo>
}
