package com.univ.doraboda.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univ.doraboda.model.Emotion
import com.univ.doraboda.model.Memo
import com.univ.doraboda.repository.EmotionRepository
import com.univ.doraboda.repository.MemoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(val memoRepository: MemoRepository, val emotionRepository: EmotionRepository): ViewModel() {
    data class CalendarState(val isLoading: Boolean = true, val memos: List<Memo>? = null, val emotions: List<Emotion>? = null, val isError: Boolean = false)
    sealed class CalendarIntent {
        data class LoadBetweenMemoAndEmotion(val date1: Long, val date2: Long): CalendarIntent()
    }
    sealed class CalendarResult{
        object Loading: CalendarResult()
        data class MemosAndEmotionsLoaded(val memos: List<Memo>?, val emotions: List<Emotion>?): CalendarResult()
        data class Error(val ex: String): CalendarResult()
    }
    private val _state = MutableStateFlow(CalendarState())
    val state: StateFlow<CalendarState> = _state

    private val dispatchers = Dispatchers.IO

    private fun reduce(result: CalendarResult): CalendarState{
        return when(result){
            is CalendarResult.Loading -> _state.value.copy(isLoading = true, isError = false)
            is CalendarResult.MemosAndEmotionsLoaded -> _state.value.copy(isLoading = false, memos = result.memos, emotions = result.emotions, isError = false)
            is CalendarResult.Error -> _state.value.copy(isLoading = false, isError = true)
        }
    }

    fun handleIntent(intent: CalendarIntent){
        viewModelScope.launch(dispatchers){
            try{
                when(intent){
                    is CalendarIntent.LoadBetweenMemoAndEmotion -> takeBetweenMemoAndEmotion(intent.date1, intent.date2)
                }
            }
            catch(e: Exception){
                _state.value = reduce(CalendarResult.Error(e.toString()))
            }
        }
    }

    private fun takeBetweenMemoAndEmotion(date1: Long, date2: Long){
        _state.value = reduce(CalendarResult.Loading)
        val memos = memoRepository.getBetween(date1, date2)
        val emotions = emotionRepository.getBetween(date1, date2)
        _state.value = reduce(CalendarResult.MemosAndEmotionsLoaded(memos, emotions))
    }
}