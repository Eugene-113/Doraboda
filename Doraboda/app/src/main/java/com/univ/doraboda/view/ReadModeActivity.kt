package com.univ.doraboda.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.univ.doraboda.R
import com.univ.doraboda.databinding.ActivityReadModeBinding
import com.univ.doraboda.intent.ReadModeIntent
import com.univ.doraboda.model.Memo
import com.univ.doraboda.repository.EmotionRepository
import com.univ.doraboda.repository.MemoRepository
import com.univ.doraboda.state.ReadModeState
import com.univ.doraboda.viewModel.ReadModeViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class ReadModeActivity : AppCompatActivity() {
    lateinit var binding: ActivityReadModeBinding
    lateinit var nonEditedDate: Date
    var thisEmo: String? = null

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if(intent != null){
                when(intent.getStringExtra("Mode")){
                    "memo" -> {
                        val memo = intent.getStringExtra("Memo").toString() //쓰기 화면에서 받아온 메모
                        val btnType = intent.getStringExtra("Btn")
                        when(btnType.toString()){
                            in "delete" -> {
                                binding.readModeTextView4.text = "작성된 메모가 없습니다."
                                viewModel.handleIntent(ReadModeIntent.DeleteMemo(nonEditedDate))
                            }
                            in "save" -> {
                                binding.readModeTextView4.text = memo
                                if(!isMemoExist){
                                    viewModel.handleIntent(ReadModeIntent.InsertMemo(Memo(nonEditedDate, memo)))
                                } else {
                                    viewModel.handleIntent(ReadModeIntent.UpdateMemo(nonEditedDate, memo))
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    lateinit var viewModel: ReadModeViewModel
    var isMemoExist = false
    var nonSlashedDate: String? = null
    var memoFlag = true
    var emotionFlag = true
    var firstMemoValue = false
    var firstEmotionValue: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        nonSlashedDate = intent.getStringExtra("Date").toString()
        val dateArr = nonSlashedDate!!.split("/")
        val dateCalendar = Calendar.getInstance()
        dateCalendar.set(dateArr.get(0).toInt(), dateArr.get(1).toInt()-1, dateArr.get(2).toInt(), 0, 0, 0)
        dateCalendar.set(Calendar.MILLISECOND, 0)
        nonEditedDate = dateCalendar.time

        binding.readModeTextView1.text = "${dateArr.get(0)}년 ${dateArr.get(1)}월 ${dateArr.get(2)}일"

        val repo1 = MemoRepository(application)
        val repo2 = EmotionRepository(application)
        viewModel = ViewModelProvider(this, ReadModeViewModel.Factory(repo1, repo2)).get(ReadModeViewModel::class.java)

        val writeModeIntent = Intent(this, WriteModeActivity::class.java)
        binding.readModeEditImageView2.setOnClickListener {
            //메모가 존재하지 않으면 빈칸 보내기, 존재하면 메모 그대로 보내기
            if(isMemoExist) writeModeIntent.putExtra("ETMemo", binding.readModeTextView4.text.toString())
            else writeModeIntent.putExtra("ETMemo", "")
            startForResult.launch(writeModeIntent)
        }

        val resIntent = Intent()
        setResult(RESULT_OK, resIntent)

        binding.readModeCardView1.setOnClickListener {
            val modal = AddEmotionFragment()
            val bundle = Bundle()
            bundle.putString("Emotion", thisEmo)
            bundle.putString("Date", nonSlashedDate)
            modal.arguments = bundle
            modal.show(supportFragmentManager, "AddEmotionFragment")
        }

        lifecycleScope.launch{
            viewModel.state.collect{
                when(it){
                    is ReadModeState.Loading -> showLoadingImage()
                    is ReadModeState.SuccessToTakeMemo -> {
                        //메모가 존재하지 않으면 '작성된 메모가 없습니다.', 존재하면 메모 그대로 출력
                        if(it.memo == null) binding.readModeTextView4.text = "작성된 메모가 없습니다."
                        else binding.readModeTextView4.text = it.memo
                        isMemoExist = it.memo != null
                        if(memoFlag){
                            firstMemoValue = isMemoExist
                            memoFlag = false
                        }
                    }
                    is ReadModeState.SuccessToTakeEmotion -> {
                        thisEmo = it.emotion
                        setImage(it.emotion)
                        if(emotionFlag){
                            firstEmotionValue = thisEmo
                            emotionFlag = false
                        }
                    }
                    is ReadModeState.SuccessToInsertMemo -> {
                        binding.readModeTextView4.text = it.memo
                        isMemoExist = true
                    }
                    is ReadModeState.SuccessToInsertEmotion -> {
                        setImage(it.emotion)
                        thisEmo = it.emotion
                    }
                    is ReadModeState.SuccessToUpdateMemo -> {
                        isMemoExist = if(it.memo == null){
                            binding.readModeTextView4.text = "작성된 메모가 없습니다."
                            false
                        }
                        else {
                            binding.readModeTextView4.text = it.memo
                            true
                        }
                    }
                    is ReadModeState.SuccessToUpdateEmotion -> {
                        setImage(it.emotion)
                        thisEmo = it.emotion
                    }
                    is ReadModeState.SuccessToDeleteMemo -> {
                        binding.readModeTextView4.text = "작성된 메모가 없습니다."
                        isMemoExist = false
                    }
                    is ReadModeState.SuccessToDeleteEmotion -> {
                        setImage(null)
                        thisEmo = null
                    }
                }
                resIntent.putExtra("DayAndExist", "${nonSlashedDate}/${firstMemoValue != isMemoExist}/${firstEmotionValue != thisEmo}")
            }
        }
        viewModel.handleIntent(ReadModeIntent.TakeMemo(nonEditedDate))
        viewModel.handleIntent(ReadModeIntent.TakeEmotion(nonEditedDate))
    }

    fun showLoadingImage(){
    }

    fun setImage(emotion: String?){
        val image = when(emotion){
            "normal" -> R.drawable.normal
            "sad" -> R.drawable.sad
            "joyful" -> R.drawable.joyful
            "angry" -> R.drawable.angry
            "confused" -> R.drawable.confused
            "happy" -> R.drawable.happy
            else -> R.drawable.icon_add
        }
        Glide.with(baseContext).load(image).into(binding.readModeEditImageView1)
    }
}