package com.univ.doraboda.calendar.db.dao

import androidx.room.*
import com.univ.doraboda.calendar.db.model.Memo
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface MemoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMemo(memo: Memo)

    @Query("SELECT * FROM memoTable WHERE id = :id")
    fun getMemo(id: Date): Flow<Memo?>

    @Query("UPDATE memoTable SET memo = :memo WHERE id = :id")
    fun updateMemo(id: Date, memo: String)

    @Query("DELETE FROM memoTable WHERE id = :id")
    fun deleteData(id: Date)

    @Query("DELETE FROM memoTable")
    fun deleteAllMemo()

    @Query("SELECT * FROM memoTable WHERE id BETWEEN :date1 AND :date2")
    fun getBetweenMemo(date1: Long, date2: Long): Flow<List<Memo>>

    @Query("SELECT * FROM memoTable")
    fun getAllMemo(): Flow<List<Memo>>
}
