package com.univ.doraboda.intent

import com.univ.doraboda.model.Memo
import java.util.Date

sealed class ReadModeIntent {
    data class TakeMemo(val id: Date): ReadModeIntent()
    data class InsertMemo(val memo: Memo): ReadModeIntent()
    data class UpdateMemo(val id: Date, val memo: String): ReadModeIntent()
    data class DeleteMemo(val memo: Memo): ReadModeIntent()
}