package com.univ.doraboda.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univ.doraboda.intent.ReadModeIntent
import com.univ.doraboda.model.Emotion
import com.univ.doraboda.model.Memo
import com.univ.doraboda.repository.EmotionRepository
import com.univ.doraboda.repository.MemoRepository
import com.univ.doraboda.state.ReadModeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ReadModeViewModel @Inject constructor(val memoRepository: MemoRepository, val emotionRepository: EmotionRepository): ViewModel() {
    private val eventChannel = Channel<ReadModeIntent>()
    val state = eventChannel.receiveAsFlow().runningFold(ReadModeState.Loading, ::reduce)
        .stateIn(viewModelScope, SharingStarted.Eagerly, ReadModeState.Loading)
    private val dispatchers = Dispatchers.IO
    private var takenMemo: Memo? = null
    private var takenEmotion: Emotion? = null
    private var takenMemos: List<Memo>? = null
    private var takenEmotions: List<Emotion>? = null

    private fun reduce(cur: ReadModeState, intent: ReadModeIntent): ReadModeState{ //상태 변화
        return when(intent){
            is ReadModeIntent.InsertMemo -> ReadModeState.SuccessToInsertMemo(intent.memo.memo)
            is ReadModeIntent.InsertEmotion -> ReadModeState.SuccessToInsertEmotion(intent.emotion.emotion)
            is ReadModeIntent.TakeMemo -> ReadModeState.SuccessToTakeMemo(takenMemo?.memo)
            is ReadModeIntent.TakeEmotion -> ReadModeState.SuccessToTakeEmotion(takenEmotion?.emotion)
            is ReadModeIntent.TakeBetweenMemoAndEmotion -> ReadModeState.SuccessToTakeBetweenMemoAndEmotion(takenMemos, takenEmotions)
            is ReadModeIntent.UpdateMemo -> ReadModeState.SuccessToUpdateMemo(intent.memo)
            is ReadModeIntent.UpdateEmotion -> ReadModeState.SuccessToUpdateEmotion(intent.Emotion)
            is ReadModeIntent.DeleteMemo -> ReadModeState.SuccessToDeleteMemo
            is ReadModeIntent.DeleteEmotion -> ReadModeState.SuccessToDeleteEmotion
        }
    }

    fun handleIntent(intent: ReadModeIntent){ //각종 비동기처리
        viewModelScope.launch {
        when(intent){
            is ReadModeIntent.TakeMemo -> takeMemo(intent.id)
            is ReadModeIntent.TakeEmotion -> takeEmotion(intent.id)
            is ReadModeIntent.TakeBetweenMemoAndEmotion -> takeBetweenMemoAndEmotion(intent.date1, intent.date2)
            is ReadModeIntent.InsertMemo -> insertMemo(intent.memo)
            is ReadModeIntent.InsertEmotion -> insertEmotion(intent.emotion)
            is ReadModeIntent.UpdateMemo -> updateMemo(intent.id, intent.memo)
            is ReadModeIntent.UpdateEmotion -> updateEmotion(intent.id, intent.Emotion)
            is ReadModeIntent.DeleteMemo -> deleteMemo(intent.date)
            is ReadModeIntent.DeleteEmotion -> deleteEmotion(intent.date)
        }
        eventChannel.send(intent)
        }
    }

    private suspend fun takeMemo(id: Date){
        withContext(dispatchers) {
            takenMemo = memoRepository.getMemo(id)
        }
    }

    private suspend fun takeEmotion(id: Date){
        withContext(dispatchers){
            takenEmotion = emotionRepository.getEmotion(id)
        }
    }

    private suspend fun takeBetweenMemoAndEmotion(date1: Long, date2: Long){
        withContext(dispatchers) {
            takenMemos = memoRepository.getBetween(date1, date2)
            takenEmotions = emotionRepository.getBetween(date1, date2)
        }
    }

    private suspend fun insertMemo(memo: Memo){
        withContext(dispatchers) {
            memoRepository.insertMemo(memo)
        }
    }

    private suspend fun insertEmotion(emotion: Emotion){
        withContext(dispatchers) {
            emotionRepository.insertEmotion(emotion)
        }
    }

    private suspend fun updateMemo(id: Date, memo: String){
        withContext(dispatchers) {
             memoRepository.updateMemo(id, memo)
         }
    }

    private suspend fun updateEmotion(id: Date, emotion: String){
        withContext(dispatchers) {
            emotionRepository.updateEmotion(id, emotion)
        }
    }

    private suspend fun deleteMemo(id: Date){
        withContext(dispatchers) {
            memoRepository.deleteMemo(id)
        }
    }

    private suspend fun deleteEmotion(id: Date){
        withContext(dispatchers) {
            emotionRepository.deleteEmotion(id)
        }
    }
}