package com.univ.doraboda.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univ.doraboda.model.Emotion
import com.univ.doraboda.model.Memo
import com.univ.doraboda.repository.DataStoreRepository
import com.univ.doraboda.repository.EmotionRepository
import com.univ.doraboda.repository.MemoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(private val memoRepository: MemoRepository, private val emotionRepository: EmotionRepository, private val dataStoreRepository: DataStoreRepository): ViewModel() {
    data class CalendarState(val isLoading: Boolean = true, val memos: List<Memo>? = null, val emotions: List<Emotion>? = null, val isError: Boolean = false, val updateID: Int = 0, val labelColorIndex: Int = -1)
    data class DateState(val startDate: Long = -1, val endDate: Long = -1)
    sealed class CalendarIntent {
        data class LoadBetweenUserData(val date1: Long, val date2: Long): CalendarIntent()
    }
    sealed class CalendarResult{
        object Loading: CalendarResult()
        data class UserDataLoaded(val memos: List<Memo>?, val emotions: List<Emotion>?, val labelColorIndex: Int): CalendarResult()
        object Error: CalendarResult()
    }
    private val dateState = MutableStateFlow(DateState())
    val state: StateFlow<CalendarState> = dateState.filter {
        it.startDate != (-1).toLong() && it.endDate != (-1).toLong()
    }.flatMapLatest { (d1, d2) ->
        combine(memoRepository.getBetween(d1, d2),
            emotionRepository.getBetween(d1, d2),
            dataStoreRepository.getLabelSetting()
        ){ memos, emotions, colorIndex ->
            CalendarResult.UserDataLoaded(memos, emotions, colorIndex)
        }.map { result ->
            reduce(result)
        }.onStart {
            emit(reduce(CalendarResult.Loading))
        }
    }.catch {
        emit(reduce(CalendarResult.Error))
    }.stateIn(scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = CalendarState()
    )

    private fun reduce(result: CalendarResult): CalendarState{
        return when(result){
            is CalendarResult.Loading -> state.value.copy(isLoading = true, isError = false)
            is CalendarResult.UserDataLoaded -> state.value.copy(isLoading = false, memos = result.memos, emotions = result.emotions, labelColorIndex = result.labelColorIndex , isError = false, updateID = state.value.updateID + 1)
            is CalendarResult.Error -> state.value.copy(isLoading = false, isError = true)
        }
    }

    fun handleIntent(intent: CalendarIntent){
        when(intent){
            is CalendarIntent.LoadBetweenUserData -> dateState.value = dateState.value.copy(intent.date1, intent.date2)
        }
    }
}