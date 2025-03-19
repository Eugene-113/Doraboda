package com.univ.doraboda.dao

import androidx.room.*
import com.univ.doraboda.model.Memo

@Dao
interface MemoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMemo(memo: Memo)

    @Query("SELECT * FROM memoTable WHERE id = :id")
    fun getMemo(id: String): Memo

    @Update
    fun updateMemo(memo: Memo)

    @Query("DELETE FROM memoTable WHERE id = :id")
    fun deleteMemo(id: String)
}
