package com.univ.doraboda.state

sealed class ReadModeState {
    data object Loading : ReadModeState()
    data class SuccessToTakeMemo(val memo: String?) : ReadModeState()
    data object SuccessToInsertMemo : ReadModeState()
    data object SuccessToUpdateMemo : ReadModeState()
    data object SuccessToDeleteMemo : ReadModeState()
    data object FailedToTakeMemo : ReadModeState()
}