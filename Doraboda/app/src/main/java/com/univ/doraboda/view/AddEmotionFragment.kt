package com.univ.doraboda.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.univ.doraboda.EmotionItem
import com.univ.doraboda.R
import com.univ.doraboda.adapter.EmotionAdapter
import com.univ.doraboda.databinding.FragmentAddEmotionBinding
import com.univ.doraboda.intent.ReadModeIntent
import com.univ.doraboda.model.Memo
import com.univ.doraboda.repository.MemoRepository
import com.univ.doraboda.viewModel.ReadModeViewModel
import timber.log.Timber
import java.util.Calendar

class AddEmotionFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentAddEmotionBinding
    lateinit var emotionAdapter: EmotionAdapter
    lateinit var emotion: String
    var bundle: Bundle? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_emotion, container, false)

        val emotionList = listOf(EmotionItem("normal", R.drawable.normal), EmotionItem("joyful", R.drawable.joyful),
            EmotionItem("happy", R.drawable.happy), EmotionItem("sad", R.drawable.sad), EmotionItem("angry", R.drawable.angry),
            EmotionItem("confused", R.drawable.confused), EmotionItem("none", R.drawable.icon_delete))

        bundle = arguments

        emotion = if(bundle?.getString("Emotion") == null) "none" else bundle?.getString("Emotion")!!
        emotionAdapter = EmotionAdapter(activity as Context, emotion)
        binding.addEmotionRecyclerView.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter = emotionAdapter
        }
        emotionAdapter.submitList(emotionList)

        return binding.root
    }

    override fun onDestroy() {
        if(emotionAdapter.thisEmotion != "unchanged" && emotionAdapter.thisEmotion != emotion){//아무것도 선택안하면 값변경 안함, 뭐라도 선택했는데 그게 처음 선택이랑 같으면 값변경 안함.
            val nonSlashedDate = bundle?.getString("Date")
            val dateArr = nonSlashedDate!!.split("/")
            val dateCalendar = Calendar.getInstance()
            dateCalendar.set(dateArr.get(0).toInt(), dateArr.get(1).toInt()-1, dateArr.get(2).toInt(), 0, 0, 0)
            dateCalendar.set(Calendar.MILLISECOND, 0)
            val nonEditedDate = dateCalendar.time
            val repo = MemoRepository(requireActivity().application)
            val viewModel: ReadModeViewModel by activityViewModels{
                ReadModeViewModel.Factory(repo)
            }
            if(bundle?.getBoolean("IsDataExist")!!) viewModel.handleIntent(ReadModeIntent.UpdateEmotion(nonEditedDate, emotionAdapter.thisEmotion))
            else viewModel.handleIntent(ReadModeIntent.InsertData(Memo(nonEditedDate, null, emotionAdapter.thisEmotion)))
        }
        super.onDestroy()
    }
}