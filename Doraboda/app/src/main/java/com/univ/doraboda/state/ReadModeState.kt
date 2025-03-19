package com.univ.doraboda.state

sealed class ReadModeState {
    object Loading : ReadModeState()
    data class SuccessToTakeMemo(val memo: String) : ReadModeState()
    object SuccessToInsertMemo : ReadModeState()
    object FailedToTakeMemo : ReadModeState()
}