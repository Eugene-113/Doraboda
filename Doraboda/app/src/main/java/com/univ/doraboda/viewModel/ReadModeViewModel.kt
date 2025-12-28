package com.univ.doraboda.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univ.doraboda.model.Emotion
import com.univ.doraboda.model.Memo
import com.univ.doraboda.repository.EmotionRepository
import com.univ.doraboda.repository.MemoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ReadModeViewModel @Inject constructor(val memoRepository: MemoRepository, val emotionRepository: EmotionRepository): ViewModel() {
    data class ReadModeState(val isLoading: Boolean = true, val memo: String? = null, val emotion: String? = null, val isError: Boolean = false)
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
    private val _state = MutableStateFlow(ReadModeState())
    val state: StateFlow<ReadModeState> = _state
    private val dispatchers = Dispatchers.IO

    private fun reduce(result: ReadModeResult): ReadModeState{ //상태 변화
        return when(result){
            is ReadModeResult.Loading -> _state.value.copy(isLoading = true, isError = false)
            is ReadModeResult.MemoAndEmotionLoaded -> _state.value.copy(isLoading = false, result.memo, result.emotion, isError = false)
            is ReadModeResult.MemoInserted -> _state.value.copy(isLoading = false, memo = result.memo, isError = false)
            is ReadModeResult.EmotionInserted -> _state.value.copy(isLoading = false, emotion = result.emotion, isError = false)
            is ReadModeResult.MemoUpdated -> _state.value.copy(isLoading = false, memo = result.memo, isError = false)
            is ReadModeResult.EmotionUpdated -> _state.value.copy(isLoading = false, emotion = result.emotion, isError = false)
            is ReadModeResult.MemoDeleted -> _state.value.copy(isLoading = false, memo = null, isError = false)
            is ReadModeResult.EmotionDeleted -> _state.value.copy(isLoading = false, emotion = null, isError = false)
            is ReadModeResult.Error -> _state.value.copy(isLoading = false, isError = true)
        }
    }

    fun handleIntent(intent: ReadModeIntent){ //각종 비동기처리
        viewModelScope.launch(dispatchers) {
            try {
                when (intent) {
                    is ReadModeIntent.LoadMemoAndEmotion -> loadMemoAndEmotion(intent.id)
                    is ReadModeIntent.UpdateMemo -> updateMemo(intent.id, intent.memo)
                    is ReadModeIntent.UpdateEmotion -> updateEmotion(intent.id, intent.emotion)
                    is ReadModeIntent.InsertMemo -> insertMemo(intent.memo)
                    is ReadModeIntent.InsertEmotion -> insertEmotion(intent.emotion)
                    is ReadModeIntent.DeleteMemo -> deleteMemo(intent.date)
                    is ReadModeIntent.DeleteEmotion -> deleteEmotion(intent.date)
                }
            } catch (e: Exception) {
                _state.value = reduce(ReadModeResult.Error(e.toString()))
                Timber.d("Error ${e.toString()}")
            }
        }
    }

    private fun loadMemoAndEmotion(id: Date){
        _state.value = reduce(ReadModeResult.Loading)
        val memo = memoRepository.getMemo(id)
        val emotion = emotionRepository.getEmotion(id)
        _state.value = reduce(ReadModeResult.MemoAndEmotionLoaded(memo?.memo, emotion?.emotion))
    }

    private fun insertMemo(memo: Memo){
        memoRepository.insertMemo(memo)
        _state.value = reduce(ReadModeResult.MemoInserted(memo.memo))
    }

    private fun insertEmotion(emotion: Emotion){
        emotionRepository.insertEmotion(emotion)
        _state.value = reduce(ReadModeResult.EmotionInserted(emotion.emotion))
    }

    private fun updateMemo(id: Date, memo: String){
        memoRepository.updateMemo(id, memo)
        _state.value = reduce(ReadModeResult.MemoUpdated(memo))
    }

    private fun updateEmotion(id: Date, emotion: String){
        emotionRepository.updateEmotion(id, emotion)
        _state.value = reduce(ReadModeResult.EmotionUpdated(emotion))
    }

    private fun deleteMemo(id: Date){
        memoRepository.deleteMemo(id)
        _state.value = reduce(ReadModeResult.MemoDeleted)
    }

    private fun deleteEmotion(id: Date){
        emotionRepository.deleteEmotion(id)
        _state.value = reduce(ReadModeResult.EmotionDeleted)
    }
}