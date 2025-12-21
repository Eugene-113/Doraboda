package com.univ.doraboda.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.univ.doraboda.model.EmotionItem
import com.univ.doraboda.R
import com.univ.doraboda.adapter.EmotionAdapter
import com.univ.doraboda.databinding.FragmentAddEmotionBinding
import com.univ.doraboda.intent.ReadModeIntent
import com.univ.doraboda.model.Emotion
import com.univ.doraboda.viewModel.ReadModeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class AddEmotionFragment : BaseBottomSheetFragment<FragmentAddEmotionBinding>() {
    lateinit var emotionAdapter: EmotionAdapter
    var emotion: String? = null
    var bundle: Bundle? = null
    override fun layoutId(): Int = R.layout.fragment_add_emotion

    override fun layoutInit(){
        val emotionList = listOf(EmotionItem("normal", R.drawable.normal), EmotionItem("joyful", R.drawable.joyful),
            EmotionItem("happy", R.drawable.happy), EmotionItem("sad", R.drawable.sad), EmotionItem("angry", R.drawable.angry),
            EmotionItem("confused", R.drawable.confused), EmotionItem(null, R.drawable.icon_delete))

        bundle = arguments

        emotion = bundle?.getString("Emotion")
        emotionAdapter = EmotionAdapter(activity as Context, emotion)
        binding.addEmotionRecyclerView.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter = emotionAdapter
        }
        emotionAdapter.submitList(emotionList)
    }

    override fun onDestroy() {
        if(emotionAdapter.thisEmotion != "unchanged" && emotionAdapter.thisEmotion != emotion){//아무것도 선택안하면 값변경 안함, 뭐라도 선택했는데 그게 처음 선택이랑 같으면 값변경 안함.
            val nonSlashedDate = bundle?.getString("Date")
            val dateArr = nonSlashedDate!!.split("/")
            val dateCalendar = Calendar.getInstance()
            dateCalendar.set(dateArr.get(0).toInt(), dateArr.get(1).toInt()-1, dateArr.get(2).toInt(), 0, 0, 0)
            dateCalendar.set(Calendar.MILLISECOND, 0)
            val nonEditedDate = dateCalendar.time
            val viewModel: ReadModeViewModel by activityViewModels()
            if(emotionAdapter.thisEmotion == null) viewModel.handleIntent(ReadModeIntent.DeleteEmotion(nonEditedDate))
            else {
                if(emotion != null) viewModel.handleIntent(ReadModeIntent.UpdateEmotion(nonEditedDate, emotionAdapter.thisEmotion!!))
                else viewModel.handleIntent(ReadModeIntent.InsertEmotion(Emotion(nonEditedDate, emotionAdapter.thisEmotion!!)))
            }
        }
        super.onDestroy()
    }
}