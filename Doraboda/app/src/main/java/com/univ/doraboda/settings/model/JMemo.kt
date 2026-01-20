package com.univ.doraboda.settings.model

import com.univ.doraboda.calendar.db.model.Memo
import java.util.Date

data class JMemo(var ID: Long, var memo: String)
fun JMemo.toMemo() = Memo(Date(ID), memo)
fun List<JMemo>.toMemoList() = map{ it.toMemo() }