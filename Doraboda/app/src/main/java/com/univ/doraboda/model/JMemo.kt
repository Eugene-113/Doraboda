package com.univ.doraboda.model

import java.util.Date

data class JMemo(var ID: Long, var memo: String)
fun JMemo.toMemo() = Memo(Date(ID), memo)
fun List<JMemo>.toMemoList() = map{ it.toMemo() }