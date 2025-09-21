package com.univ.doraboda.intent

import com.univ.doraboda.model.Emotion
import com.univ.doraboda.model.Memo
import java.util.Date

sealed class ReadModeIntent {
    data class TakeMemo(val id: Date): ReadModeIntent()
    data class TakeEmotion(val id: Date): ReadModeIntent()
    data class UpdateEmotion(val id: Date, val Emotion: String): ReadModeIntent()
    data class InsertMemo(val memo: Memo): ReadModeIntent()
    data class InsertEmotion(val emotion: Emotion): ReadModeIntent()
    data class UpdateMemo(val id: Date, val memo: String): ReadModeIntent()
    data class DeleteMemo(val date: Date): ReadModeIntent()
    data class DeleteEmotion(val date: Date): ReadModeIntent()
}