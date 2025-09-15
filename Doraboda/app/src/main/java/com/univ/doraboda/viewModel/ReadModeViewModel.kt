package com.univ.doraboda.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import java.util.Date

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
            is ReadModeIntent.InsertData -> {
                if(intent.memo.emotion != null) ReadModeState.SuccessToInsertData("e", intent.memo.emotion!!)
                else ReadModeState.SuccessToInsertData("m", intent.memo.memo!!)
            }
            is ReadModeIntent.TakeData -> {
                if(takenMemo == null) ReadModeState.SuccessToTakeData(null, null, 0)
                else ReadModeState.SuccessToTakeData(takenMemo!!.memo, takenMemo!!.emotion, 1)
            }
            is ReadModeIntent.UpdateMemo -> ReadModeState.SuccessToUpdateMemo(intent.memo)
            is ReadModeIntent.DeleteData -> ReadModeState.SuccessToDeleteData
            is ReadModeIntent.UpdateEmotion -> ReadModeState.SuccessToUpdateEmotion(intent.Emotion)
        }
    }

    fun handleIntent(intent: ReadModeIntent){ //각종 비동기처리
        viewModelScope.launch {
        when(intent){
            is ReadModeIntent.InsertData -> insertData(intent.memo)
            is ReadModeIntent.TakeData -> takeMemo(intent.id)
            is ReadModeIntent.UpdateMemo -> updateMemo(intent.id, intent.memo)
            is ReadModeIntent.UpdateEmotion -> updateEmotion(intent.id, intent.Emotion)
            is ReadModeIntent.DeleteData -> deleteData(intent.date)
        }
        eventChannel.send(intent)
        }
    }

    private suspend fun takeMemo(id: Date){
        withContext(dispatchers) {
            takenMemo = repository.get(id)
        }
    }

    private suspend fun insertData(memo: Memo){
        withContext(dispatchers) {
            repository.insertData(memo)
        }
    }

    private suspend fun updateMemo(id: Date, memo: String?){
        withContext(dispatchers) {
             repository.updateMemo(id, memo)
         }
    }

    private suspend fun updateEmotion(id: Date, emotion: String?){
        withContext(dispatchers) {
            repository.updateEmotion(id, emotion)
        }
    }

    private suspend fun deleteData(id: Date){
        withContext(dispatchers) {
            repository.deleteData(id)
        }
    }
}