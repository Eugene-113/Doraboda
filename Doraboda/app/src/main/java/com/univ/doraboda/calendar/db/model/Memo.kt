package com.univ.doraboda.calendar.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.univ.doraboda.settings.model.JMemo
import java.util.Date

@Entity(tableName = "memoTable")
data class Memo (
    @PrimaryKey var ID: Date,
    var memo: String
)

fun Memo.toJMemo() = JMemo(ID.time, memo)
fun List<Memo>.toJMemoList() = map{ it.toJMemo() }