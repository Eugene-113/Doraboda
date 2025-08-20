package com.univ.doraboda.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.univ.doraboda.intent.ReadModeIntent
import com.univ.doraboda.model.Memo
import com.univ.doraboda.repository.MemoRepository
import com.univ.doraboda.state.ReadModeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReadModeViewModel(val repository: MemoRepository): ViewModel() {
    private val eventChannel = Channel<ReadModeIntent>()
    val state = eventChannel.receiveAsFlow().runningFold(ReadModeState.Loading, ::reduce)
        .stateIn(viewModelScope, SharingStarted.Eagerly, ReadModeState.Loading)
    private val dispatchers = Dispatchers.IO
    private var takenMemo: Memo? = null

    class Factory(private val repo: MemoRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(ReadModeViewModel::class.java)){
                return ReadModeViewModel(repo) as T
            }
            throw IllegalArgumentException("")
        }
    }

    private fun reduce(cur: ReadModeState, intent: ReadModeIntent): ReadModeState{ //상태 변화
        return when(intent){
            is ReadModeIntent.InsertMemo -> {
                ReadModeState.SuccessToInsertMemo
            }
            is ReadModeIntent.TakeMemo -> {
                if(takenMemo == null) ReadModeState.FailedToTakeMemo
                else ReadModeState.SuccessToTakeMemo(takenMemo!!.memo)
            }
            is ReadModeIntent.UpdateMemo -> {
                ReadModeState.SuccessToUpdateMemo
            }
            is ReadModeIntent.DeleteMemo -> {
                ReadModeState.SuccessToDeleteMemo
            }
        }
    }

    fun handleIntent(intent: ReadModeIntent){ //각종 비동기처리
        viewModelScope.launch {
        when(intent){
            is ReadModeIntent.InsertMemo -> {
                insertMemo(intent.memo)
            }
            is ReadModeIntent.TakeMemo -> {
                takeMemo(intent.id)
            }
            is ReadModeIntent.UpdateMemo -> {
                updateMemo(intent.id, intent.memo)
            }
            is ReadModeIntent.DeleteMemo -> {
                deleteMemo(intent.memo)
            }
        }
        eventChannel.send(intent)
        }
    }

    private suspend fun takeMemo(id: String){
        return withContext(dispatchers) {
            takenMemo = repository.get(id)
        }
    }

    private suspend fun insertMemo(memo: Memo){
        return withContext(dispatchers) {
            repository.insert(memo)
        }
    }

    private suspend fun updateMemo(id: String, memo: String){
        return withContext(dispatchers) {
             repository.update(id, memo)
         }
    }

    private suspend fun deleteMemo(memo: Memo){
        return withContext(dispatchers) {
            repository.delete(memo)
        }
    }
}