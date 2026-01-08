package com.univ.doraboda.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.univ.doraboda.model.DoraData
import com.univ.doraboda.model.Emotion
import com.univ.doraboda.model.Memo
import com.univ.doraboda.model.toEmotionList
import com.univ.doraboda.model.toJEmotionList
import com.univ.doraboda.model.toJMemoList
import com.univ.doraboda.model.toMemoList
import com.univ.doraboda.repository.DataStoreRepository
import com.univ.doraboda.repository.EmotionRepository
import com.univ.doraboda.repository.FileRepository
import com.univ.doraboda.repository.MemoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val dataStoreRepository: DataStoreRepository, private val memoRepository: MemoRepository,
                                            private val emotionRepository: EmotionRepository, private val fileRepository: FileRepository): ViewModel() {
    data class SettingsState(val labelType: Int = -1, val isQuoteModeOn: Boolean? = null, val memos: List<Memo>? = null, val emotions: List<Emotion>? = null, val loadNew: Boolean = false)
    sealed class SettingsIntent {
        data class SetLabelType(val labelType: Int): SettingsIntent()
        data class SetQuoteMode(val isQuoteModeOn: Boolean): SettingsIntent()
        data class SaveAllDoraData(val folderURI: Uri): SettingsIntent()
        data class LoadAllDoraData(val fileURI: Uri): SettingsIntent()
        object LoadAllFirst: SettingsIntent()
    }
    sealed class SettingsResult{
        data class SettingsDataLoaded(val labelType: Int, val isQuoteModeOn: Boolean, val memos: List<Memo>, val emotions: List<Emotion>): SettingsResult()
        data class NewSettingsDataLoaded(val labelType: Int, val isQuoteModeOn: Boolean, val memos: List<Memo>, val emotions: List<Emotion>): SettingsResult()
        data class SetQuoteMode(val quoteMode: Boolean): SettingsResult()
        data class SetLabelIndex(val labelIndex: Int): SettingsResult()
    }
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()
    private val _effect = MutableSharedFlow<String>()
    val errorEvents: SharedFlow<String> = _effect.asSharedFlow()

    private fun reduce(result: SettingsResult): SettingsState{
        return when(result){
            is SettingsResult.SettingsDataLoaded -> _state.value.copy(labelType = result.labelType, isQuoteModeOn = result.isQuoteModeOn, memos = result.memos, emotions = result.emotions, loadNew = false)
            is SettingsResult.NewSettingsDataLoaded -> _state.value.copy(labelType = result.labelType, isQuoteModeOn = result.isQuoteModeOn, memos = result.memos, emotions = result.emotions, loadNew = true)
            is SettingsResult.SetQuoteMode -> _state.value.copy(isQuoteModeOn = result.quoteMode, loadNew = false)
            is SettingsResult.SetLabelIndex -> _state.value.copy(labelType = result.labelIndex, loadNew = false)
        }
    }

    fun handleIntent(intent: SettingsIntent){
        when (intent) {
            is SettingsIntent.SetLabelType -> setLabelType(intent.labelType)
            is SettingsIntent.SetQuoteMode -> setQuoteMode(intent.isQuoteModeOn)
            is SettingsIntent.SaveAllDoraData -> saveDoraData(intent.folderURI)
            is SettingsIntent.LoadAllDoraData -> loadDoraData(intent.fileURI)
            is SettingsIntent.LoadAllFirst -> loadAllFirstTime()
        }
    }


    private fun saveDoraData(fileURI: Uri){
        viewModelScope.launch(Dispatchers.IO) {
            if(_state.value.memos != null && _state.value.emotions != null && _state.value.labelType != -1
                && _state.value.isQuoteModeOn != null){
                try {
                    fileRepository.jsonFileSave(fileURI, toJson(DoraData(_state.value.memos!!.toJMemoList(),
                        _state.value.emotions!!.toJEmotionList(), _state.value.labelType,
                        _state.value.isQuoteModeOn!!)))
                }catch (e: Exception){
                    _effect.emit(e.message.toString())
                }
            }
        }
    }

    private fun loadDoraData(uri: Uri){
        viewModelScope.launch(Dispatchers.IO) {
            val doraData: DoraData
            try {
                val json = fileRepository.jsonFileRead(uri)
                doraData = fromJson(json)
            }catch (e: Exception){
                Timber.d("uri ${e}")
                _effect.emit("이 파일은 변환할 수 없습니다.")
                return@launch
            }
            try {
                memoRepository.deleteAllMemo()
                emotionRepository.deleteAllEmotion()

                val memos = doraData.memos.toMemoList()
                for(memo in memos){
                    memoRepository.insertMemo(memo)
                }
                val emotions = doraData.emotions.toEmotionList()
                for(emotion in emotions){
                    emotionRepository.insertEmotion(emotion)
                }
                dataStoreRepository.setQuoteSetting(doraData.quoteMode)
                dataStoreRepository.setLabelSetting(doraData.labelColor)

                _state.value = reduce(SettingsResult.NewSettingsDataLoaded(labelType = doraData.labelColor, isQuoteModeOn = doraData.quoteMode, memos = memos, emotions = emotions))
                _effect.emit("데이터를 가져오는 데에 성공했습니다.")
            }catch (e: Exception){
                _effect.emit(e.message.toString())
            }
        }
    }

    private fun loadAllFirstTime(){
        viewModelScope.launch {
            try {
                val labelIndex = dataStoreRepository.getLabelSetting().first()
                val quoteMode = dataStoreRepository.getQuoteSetting().first()
                val memos = memoRepository.getAllMemo().first()
                val emotions = emotionRepository.getAllEmotion().first()
                _state.value = reduce(SettingsResult.SettingsDataLoaded(labelType = labelIndex, isQuoteModeOn = quoteMode, memos = memos, emotions = emotions))
            } catch (e: Exception){
                _effect.emit("${e.message}")
            }
        }
    }

    private fun toJson(doraData: DoraData): String{
        val gson = GsonBuilder().create()
        return gson.toJson(doraData)
    }

    private fun fromJson(json: String): DoraData{
        val gson = GsonBuilder().create()
        return gson.fromJson(json, DoraData::class.java)
    }

    private fun setLabelType(labelType: Int){
        viewModelScope.launch(Dispatchers.IO){
            try {
                dataStoreRepository.setLabelSetting(labelType)
                _state.value = reduce(SettingsResult.SetLabelIndex(labelType))
            } catch (e: Exception){
                _effect.emit(e.message.toString())
            }
        }
    }

    private fun setQuoteMode(isQuoteModeOn: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dataStoreRepository.setQuoteSetting(isQuoteModeOn)
                _state.value = reduce(SettingsResult.SetQuoteMode(isQuoteModeOn))
            } catch (e: Exception){
                _effect.emit(e.message.toString())
            }
        }
    }
}