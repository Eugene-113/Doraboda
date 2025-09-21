package com.univ.doraboda.state

sealed class ReadModeState {
    data object Loading : ReadModeState()
    data class SuccessToTakeMemo(val memo: String?) : ReadModeState()
    data class SuccessToTakeEmotion(val emotion: String?) : ReadModeState()
    data class SuccessToInsertMemo(val memo: String): ReadModeState()
    data class SuccessToInsertEmotion(val emotion: String): ReadModeState()
    data class SuccessToUpdateEmotion(val emotion: String?): ReadModeState()
    data class SuccessToUpdateMemo(val memo: String?): ReadModeState()
    data object SuccessToDeleteMemo : ReadModeState()
    data object SuccessToDeleteEmotion : ReadModeState()
}