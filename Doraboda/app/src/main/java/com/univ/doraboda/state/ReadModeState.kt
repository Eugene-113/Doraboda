package com.univ.doraboda.state

sealed class ReadModeState {
    data object Loading : ReadModeState()
    data class SuccessToTakeData(val memo: String?, val emotion: String?, val isDataExist: Int) : ReadModeState()
    data class SuccessToInsertData(val mOrE: String, val info: String): ReadModeState()
    data object SuccessToDeleteData : ReadModeState()
    data class SuccessToUpdateEmotion(val emotion: String?): ReadModeState()
    data class SuccessToUpdateMemo(val memo: String?): ReadModeState()
}