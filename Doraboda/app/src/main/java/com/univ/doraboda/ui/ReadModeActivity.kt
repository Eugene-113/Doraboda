package com.univ.doraboda.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.univ.doraboda.R
import com.univ.doraboda.databinding.ActivityReadModeBinding
import com.univ.doraboda.model.Memo
import com.univ.doraboda.viewModel.ReadModeViewModel
import com.univ.doraboda.viewModel.ReadModeViewModel.ReadModeIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import kotlin.getValue

@AndroidEntryPoint
class ReadModeActivity : AppCompatActivity() {
    lateinit var binding: ActivityReadModeBinding
    lateinit var nonEditedDate: Date
    var thisEmo: String? = null

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
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

    val viewModel: ReadModeViewModel by viewModels()
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

        val writeModeIntent = Intent(this, WriteModeActivity::class.java)
        binding.readModeEditImageView2.setOnClickListener {
            //메모가 존재하지 않으면 빈칸 보내기, 존재하면 메모 그대로 보내기
            if(isMemoExist) writeModeIntent.putExtra("ETMemo", binding.readModeTextView4.text.toString())
            else writeModeIntent.putExtra("ETMemo", "")
            startForResult.launch(writeModeIntent)
        }

        val resIntent = Intent()
        setResult(RESULT_OK, resIntent)

        binding.readModeEditImageView1.setOnClickListener {
            val modal = AddEmotionFragment()
            val bundle = Bundle()
            bundle.putString("Emotion", thisEmo)
            bundle.putString("Date", nonSlashedDate)
            modal.arguments = bundle
            modal.show(supportFragmentManager, "AddEmotionFragment")
        }

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.state.collect{
                    if(it.isLoading){
                    } else{
                        if(it.isError){
                        } else{
                            if(it.memo == null) binding.readModeTextView4.text = "작성된 메모가 없습니다."
                            else binding.readModeTextView4.text = it.memo

                            thisEmo = it.emotion
                            setImage(it.emotion)

                            isMemoExist = it.memo != null

                            if(memoFlag){
                                firstMemoValue = isMemoExist
                                memoFlag = false
                            }
                            if(emotionFlag){
                                firstEmotionValue = thisEmo
                                emotionFlag = false
                            }
                            resIntent.putExtra("DayAndExist", "${nonSlashedDate}/${firstMemoValue != isMemoExist}/${firstEmotionValue != thisEmo}")
                        }
                    }
                }
            }
        }
        viewModel.handleIntent(ReadModeIntent.LoadMemoAndEmotion(nonEditedDate))
    }

    fun showLoadingImage(){
    }

    fun setImage(emotion: String?){
        val image = when(emotion){
            "normal" -> R.drawable.icon_normal
            "sad" -> R.drawable.icon_sad
            "joyful" -> R.drawable.icon_joy
            "angry" -> R.drawable.icon_angry
            "confused" -> R.drawable.icon_confused
            "happy" -> R.drawable.icon_happy
            else -> R.drawable.icon_empty
        }
        Glide.with(baseContext).load(image).into(binding.readModeEditImageView1)
    }
}