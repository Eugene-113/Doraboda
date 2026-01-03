package com.univ.doraboda.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univ.doraboda.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(val dataStoreRepository: DataStoreRepository): ViewModel() {
    data class SettingsState(val labelType: Int = -1, val isQuoteModeOn: Boolean? = null, val isError: Boolean = false)
    sealed class SettingsIntent {
        data class SetLabelType(val labelType: Int): SettingsIntent()
        data class SetQuoteMode(val isQuoteModeOn: Boolean): SettingsIntent()
    }
    sealed class SettingsResult{
        data class SettingsDataLoaded(val labelType: Int, val isQuoteModeOn: Boolean): SettingsResult()
        object Error: SettingsResult()
    }
    private val _state = combine(dataStoreRepository.getLabelSetting(), dataStoreRepository.getQuoteSetting()){ labelIndex, quoteMode ->
        labelIndex to quoteMode
    }.map { pair ->
        reduce(SettingsResult.SettingsDataLoaded(pair.first, pair.second))
    }.catch {
        emit(reduce(SettingsResult.Error))
    }.stateIn(scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = SettingsState()
        )
    val state: StateFlow<SettingsState> = _state

    private fun reduce(result: SettingsResult): SettingsState{
        return when(result){
            is SettingsResult.SettingsDataLoaded -> _state.value.copy(labelType = result.labelType, isError = false, isQuoteModeOn = result.isQuoteModeOn)
            is SettingsResult.Error -> _state.value.copy(isError = true)
        }
    }

    fun handleIntent(intent: SettingsIntent){
        when (intent) {
            is SettingsIntent.SetLabelType -> setLabelType(intent.labelType)
            is SettingsIntent.SetQuoteMode -> setQuoteMode(intent.isQuoteModeOn)
        }
    }

    fun setLabelType(labelType: Int){
        dataStoreRepository.setLabelSetting(labelType)
    }

    fun setQuoteMode(isQuoteModeOn: Boolean){
        dataStoreRepository.setQuoteSetting(isQuoteModeOn)
    }
}