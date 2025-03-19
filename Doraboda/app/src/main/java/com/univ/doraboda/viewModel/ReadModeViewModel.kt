package com.univ.doraboda.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.univ.doraboda.intent.ReadModeIntent
import com.univ.doraboda.model.Memo
import com.univ.doraboda.repository.MemoRepository
import com.univ.doraboda.state.ReadModeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReadModeViewModel(val repository: MemoRepository): ViewModel() {
    val _state = MutableStateFlow<ReadModeState>(ReadModeState.Loading)
    val state: StateFlow<ReadModeState>
        get() = _state
    val dispatchers = Dispatchers.IO

    class Factory(private val repo: MemoRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(ReadModeViewModel::class.java)){
                return ReadModeViewModel(repo) as T
            }
            throw IllegalArgumentException("")
        }
    }


    fun handleIntent(intent: ReadModeIntent){
        when(intent){
            is ReadModeIntent.InsertMemo -> insertMemo(intent.memo)
            is ReadModeIntent.TakeMemo -> takeMemo(intent.id)
            is ReadModeIntent.TakeEmotions -> takeEmotions()
        }
    }

    fun takeMemo(id: String){
        viewModelScope.launch(dispatchers) {
            val testState = repository.get(id)
            if(testState == null) _state.value = ReadModeState.FailedToTakeMemo
            else _state.value = ReadModeState.SuccessToTakeMemo(testState.memo)
        }
    }

    fun insertMemo(memo: Memo){
        viewModelScope.launch(dispatchers) {
            repository.insert(memo)
            _state.value = ReadModeState.SuccessToInsertMemo
        }
    }

    fun takeEmotions(){
    }
}