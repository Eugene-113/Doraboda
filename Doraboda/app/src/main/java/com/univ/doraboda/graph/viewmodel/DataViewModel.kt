package com.univ.doraboda.graph.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univ.doraboda.calendar.db.model.Emotion
import com.univ.doraboda.calendar.repository.EmotionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(private val emotionRepository: EmotionRepository): ViewModel() {
    data class DataState(val isLoading: Boolean = true, val emotions: List<Emotion>? = null, val isError: Boolean = false)
    data class DateState(val startDate: Long = -1, val endDate: Long = -1)
    sealed class DataIntent{
        data class LoadBetweenEmotions(val date1: Long, val date2: Long): DataIntent()
    }
    sealed class DataResult{
        object Error: DataResult()
        object Loading: DataResult()
        data class EmotionsLoaded(val emotions: List<Emotion>): DataResult()
    }
    private val dateState = MutableStateFlow(DateState())
    val state = dateState.filter {
        it.startDate != (-1).toLong() && it.endDate != (-1).toLong()
    }.flatMapLatest { (d1, d2) ->
        emotionRepository.getBetween(d1, d2).map { emotions ->
            reduce(DataResult.EmotionsLoaded(emotions))
        }.onStart {
            emit(reduce(DataResult.Loading))
        }
    }.catch {
        emit(reduce(DataResult.Error))
    }.stateIn(scope = viewModelScope,
            started = SharingStarted.Companion.Lazily,
            initialValue = DataState()
        )

    private fun reduce(result: DataResult): DataState{
        return when(result){
            is DataResult.EmotionsLoaded -> state.value.copy(isError = false, emotions = result.emotions, isLoading = false)
            is DataResult.Error -> state.value.copy(isError = true, isLoading = false)
            is DataResult.Loading -> state.value.copy(isLoading = true)
        }
    }

    fun handleIntent(intent: DataIntent){
        when(intent){
            is DataIntent.LoadBetweenEmotions -> dateState.value = dateState.value.copy(intent.date1, intent.date2)
        }
    }
}