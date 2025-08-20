package com.univ.doraboda.intent

import com.univ.doraboda.model.Memo

sealed class ReadModeIntent {
    data class TakeMemo(val id: String): ReadModeIntent()
    data class InsertMemo(val memo: Memo): ReadModeIntent()
    data class UpdateMemo(val id: String, val memo: String): ReadModeIntent()
    data class DeleteMemo(val memo: Memo): ReadModeIntent()
}