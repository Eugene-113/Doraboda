package com.univ.doraboda.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univ.doraboda.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val dataStoreRepository: DataStoreRepository): ViewModel() {
    data class SettingsState(val labelType: Int = -1, val isQuoteModeOn: Boolean? = null, val isError: Boolean = false)
    sealed class SettingsIntent {
        data class SetLabelType(val labelType: Int): SettingsIntent()
        data class SetQuoteMode(val isQuoteModeOn: Boolean): SettingsIntent()
    }
    sealed class SettingsResult{
        data class SettingsDataLoaded(val labelType: Int, val isQuoteModeOn: Boolean): SettingsResult()
        object Error: SettingsResult()
    }
    val state = combine(dataStoreRepository.getLabelSetting(), dataStoreRepository.getQuoteSetting()){ labelIndex, quoteMode ->
        labelIndex to quoteMode
    }.map { pair ->
        reduce(SettingsResult.SettingsDataLoaded(pair.first, pair.second))
    }.catch {
        emit(reduce(SettingsResult.Error))
    }.stateIn(scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = SettingsState()
        )
    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents: SharedFlow<String> = _errorEvents.asSharedFlow()

    private fun reduce(result: SettingsResult): SettingsState{
        return when(result){
            is SettingsResult.SettingsDataLoaded -> state.value.copy(labelType = result.labelType, isError = false, isQuoteModeOn = result.isQuoteModeOn)
            is SettingsResult.Error -> state.value.copy(isError = true)
        }
    }

    fun handleIntent(intent: SettingsIntent){
        try {
            when (intent) {
                is SettingsIntent.SetLabelType -> setLabelType(intent.labelType)
                is SettingsIntent.SetQuoteMode -> setQuoteMode(intent.isQuoteModeOn)
            }
        } catch (e: Exception) {
            viewModelScope.launch {
                _errorEvents.emit(e.toString())
            }
        }
    }

    private fun setLabelType(labelType: Int){
        dataStoreRepository.setLabelSetting(labelType)
    }

    private fun setQuoteMode(isQuoteModeOn: Boolean){
        dataStoreRepository.setQuoteSetting(isQuoteModeOn)
    }
}