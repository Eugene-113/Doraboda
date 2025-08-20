package com.univ.doraboda.dao

import androidx.room.*
import com.univ.doraboda.model.Memo

@Dao
interface MemoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMemo(memo: Memo)

    @Query("SELECT * FROM memoTable WHERE id = :id")
    fun getMemo(id: String): Memo

    @Query("UPDATE memoTable SET memo = :memo WHERE id = :id")
    fun updateMemo(id: String, memo: String)

    @Delete
    fun deleteMemo(memo: Memo)
}
