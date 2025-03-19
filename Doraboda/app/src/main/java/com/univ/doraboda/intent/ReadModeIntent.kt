package com.univ.doraboda.intent

import com.univ.doraboda.model.Memo

sealed class ReadModeIntent {
    data class TakeMemo(val id: String): ReadModeIntent()
    data class InsertMemo(val memo: Memo): ReadModeIntent()
    object TakeEmotions: ReadModeIntent()
}