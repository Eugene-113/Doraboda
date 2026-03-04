package com.univ.doraboda.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univ.doraboda.calendar.db.model.Emotion
import com.univ.doraboda.calendar.db.model.Memo
import com.univ.doraboda.calendar.repository.EmotionRepository
import com.univ.doraboda.calendar.repository.MemoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ReadModeViewModel @Inject constructor(private val memoRepository: MemoRepository, private val emotionRepository: EmotionRepository): ViewModel() {
    data class ReadModeState(val isLoading: Boolean = true, val memo: String? = null, val emotion: String? = null, val isError: Boolean = false)
    data class DateState(val date: Date? = null)
    sealed class ReadModeIntent {
        data class LoadMemoAndEmotion(val id: Date): ReadModeIntent()
        data class UpdateEmotion(val id: Date, val emotion: String): ReadModeIntent()
        data class UpdateMemo(val id: Date, val memo: String): ReadModeIntent()
        data class InsertMemo(val memo: Memo): ReadModeIntent()
        data class InsertEmotion(val emotion: Emotion): ReadModeIntent()
        data class DeleteMemo(val date: Date): ReadModeIntent()
        data class DeleteEmotion(val date: Date): ReadModeIntent()
    }
    sealed class ReadModeResult{
        object Loading: ReadModeResult()
        data class MemoAndEmotionLoaded(val memo: String?, val emotion: String?): ReadModeResult()
        data class MemoInserted(val memo: String): ReadModeResult()
        data class EmotionInserted(val emotion: String): ReadModeResult()
        data class MemoUpdated(val memo: String): ReadModeResult()
        data class EmotionUpdated(val emotion: String): ReadModeResult()
        object MemoDeleted: ReadModeResult()
        object EmotionDeleted: ReadModeResult()
        data class Error(val ex: String): ReadModeResult()
    }
    private val dateState = MutableStateFlow(DateState())
    val state: StateFlow<ReadModeState> = dateState.filter {
        it.date != null
    }.flatMapLatest { (date) ->
        combine(memoRepository.getMemo(date!!), emotionRepository.getEmotion(date)){ memo, emotion ->
            ReadModeResult.MemoAndEmotionLoaded(memo?.memo, emotion?.emotion)
        }.map { result ->
            reduce(result, state.value)
        }.onStart {
            emit(reduce(ReadModeResult.Loading, state.value))
        }
    }.catch { e ->
        emit(reduce(ReadModeResult.Error(e.message.toString()), state.value))
    }.stateIn(scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ReadModeState()
            )
    private val dispatchers = Dispatchers.IO
    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents: SharedFlow<String> = _errorEvents.asSharedFlow()

    private fun reduce(result: ReadModeResult, thisState: ReadModeState): ReadModeState{ //상태 변화
        return when(result){
            is ReadModeResult.Loading -> thisState.copy(isLoading = true, isError = false)
            is ReadModeResult.MemoAndEmotionLoaded -> thisState.copy(isLoading = false, result.memo, result.emotion, isError = false)
            is ReadModeResult.MemoInserted -> thisState.copy(isLoading = false, memo = result.memo, isError = false)
            is ReadModeResult.EmotionInserted -> thisState.copy(isLoading = false, emotion = result.emotion, isError = false)
            is ReadModeResult.MemoUpdated -> thisState.copy(isLoading = false, memo = result.memo, isError = false)
            is ReadModeResult.EmotionUpdated -> thisState.copy(isLoading = false, emotion = result.emotion, isError = false)
            is ReadModeResult.MemoDeleted -> thisState.copy(isLoading = false, memo = null, isError = false)
            is ReadModeResult.EmotionDeleted -> thisState.copy(isLoading = false, emotion = null, isError = false)
            is ReadModeResult.Error -> thisState.copy(isLoading = false, isError = true)
        }
    }

    fun handleIntent(intent: ReadModeIntent){ //각종 비동기처리
        viewModelScope.launch(dispatchers) {
            try {
                when (intent) {
                    is ReadModeIntent.LoadMemoAndEmotion -> dateState.value = dateState.value.copy(intent.id)
                    is ReadModeIntent.UpdateMemo -> updateMemo(intent.id, intent.memo)
                    is ReadModeIntent.UpdateEmotion -> updateEmotion(intent.id, intent.emotion)
                    is ReadModeIntent.InsertMemo -> insertMemo(intent.memo)
                    is ReadModeIntent.InsertEmotion -> insertEmotion(intent.emotion)
                    is ReadModeIntent.DeleteMemo -> deleteMemo(intent.date)
                    is ReadModeIntent.DeleteEmotion -> deleteEmotion(intent.date)
                }
            } catch (e: Exception) {
                _errorEvents.emit(e.message.toString())
            }
        }
    }

    private fun insertMemo(memo: Memo){
        memoRepository.insertMemo(memo)
    }

    private fun insertEmotion(emotion: Emotion){
        emotionRepository.insertEmotion(emotion)
    }

    private fun updateMemo(id: Date, memo: String){
        memoRepository.updateMemo(id, memo)
    }

    private fun updateEmotion(id: Date, emotion: String){
        emotionRepository.updateEmotion(id, emotion)
    }

    private fun deleteMemo(id: Date){
        memoRepository.deleteMemo(id)
    }

    private fun deleteEmotion(id: Date){
        emotionRepository.deleteEmotion(id)
    }
}