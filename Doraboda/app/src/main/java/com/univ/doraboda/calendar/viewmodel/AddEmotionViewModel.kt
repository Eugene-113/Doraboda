package com.univ.doraboda.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univ.doraboda.calendar.db.model.Emotion
import com.univ.doraboda.calendar.repository.EmotionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddEmotionViewModel @Inject constructor(private val emotionRepository: EmotionRepository): ViewModel() {
    sealed class AddEmotionIntent{
        data class DeleteEmotion(val date: Date): AddEmotionIntent()
        data class UpdateEmotion(val id: Date, val emotion: String): AddEmotionIntent()
        data class InsertEmotion(val emotion: Emotion): AddEmotionIntent()
    }

    fun handleIntent(intent: AddEmotionIntent){
        when(intent){
            is AddEmotionIntent.InsertEmotion -> insertEmotion(intent.emotion)
            is AddEmotionIntent.UpdateEmotion -> updateEmotion(intent.id, intent.emotion)
            is AddEmotionIntent.DeleteEmotion -> deleteEmotion(intent.date)
        }
    }

    private fun insertEmotion(emotion: Emotion){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                emotionRepository.insertEmotion(emotion)
            }catch(e: Exception){
                Timber.d("error in AddEmotionViewModel insert")
            }
        }
    }

    private fun deleteEmotion(id: Date){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                emotionRepository.deleteEmotion(id)
            }catch(e: Exception){
                Timber.d("error in AddEmotionViewModel delete")
            }
        }
    }

    private fun updateEmotion(id: Date, emotion: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                emotionRepository.updateEmotion(id, emotion)
            }catch(e: Exception){
                Timber.d("error in AddEmotionViewModel update")
            }
        }
    }
}