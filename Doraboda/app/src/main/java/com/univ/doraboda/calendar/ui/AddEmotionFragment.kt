package com.univ.doraboda.calendar.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.univ.doraboda.calendar.model.EmotionItem
import com.univ.doraboda.R
import com.univ.doraboda.databinding.FragmentAddEmotionBinding
import com.univ.doraboda.calendar.db.model.Emotion
import com.univ.doraboda.app.base.BaseBottomSheetFragment
import com.univ.doraboda.calendar.viewmodel.AddEmotionViewModel
import com.univ.doraboda.calendar.viewmodel.AddEmotionViewModel.AddEmotionIntent
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class AddEmotionFragment : BaseBottomSheetFragment<FragmentAddEmotionBinding>() {
    lateinit var emotionAdapter: EmotionAdapter
    var emotion: String? = null
    var bundle: Bundle? = null
    override fun layoutId(): Int = R.layout.fragment_add_emotion

    override fun layoutInit(){

        val emotionList = listOf(EmotionItem("normal", R.drawable.icon_normal), EmotionItem("joyful", R.drawable.icon_joy),
            EmotionItem("happy", R.drawable.icon_happy), EmotionItem("sad", R.drawable.icon_sad), EmotionItem("angry", R.drawable.icon_angry),
            EmotionItem("confused", R.drawable.icon_confused), EmotionItem(null, R.drawable.icon_empty))

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
            val viewModel: AddEmotionViewModel by viewModels()
            if(emotionAdapter.thisEmotion == null) viewModel.handleIntent(AddEmotionIntent.DeleteEmotion(nonEditedDate))
            else {
                if(emotion != null) viewModel.handleIntent(AddEmotionIntent.UpdateEmotion(nonEditedDate, emotionAdapter.thisEmotion!!))
                else viewModel.handleIntent(AddEmotionIntent.InsertEmotion(Emotion(nonEditedDate, emotionAdapter.thisEmotion!!)))
            }
        }
        super.onDestroy()
    }
}