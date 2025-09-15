package com.univ.doraboda.intent

import com.univ.doraboda.model.Memo
import java.util.Date

sealed class ReadModeIntent {
    data class TakeData(val id: Date): ReadModeIntent()
    data class UpdateEmotion(val id: Date, val Emotion: String?): ReadModeIntent()
    data class InsertData(val memo: Memo): ReadModeIntent()
    data class UpdateMemo(val id: Date, val memo: String?): ReadModeIntent()
    data class DeleteData(val date: Date): ReadModeIntent()
}