package com.univ.doraboda.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.univ.doraboda.R
import com.univ.doraboda.databinding.ActivityReadModeBinding
import com.univ.doraboda.intent.ReadModeIntent
import com.univ.doraboda.model.Memo
import com.univ.doraboda.repository.MemoRepository
import com.univ.doraboda.state.ReadModeState
import com.univ.doraboda.viewModel.ReadModeViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class ReadModeActivity : AppCompatActivity() {
    lateinit var binding: ActivityReadModeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_mode)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_read_mode)

        val intent = intent
        val nonEditedDate = intent.getStringExtra("Date")
        val date = nonEditedDate!!.split("/")

        binding.readModeTextView1.text = "${date.get(0)}년 ${date.get(1)}월 ${date.get(2)}일"
        val writeModeIntent = Intent(this, WriteModeActivity::class.java)
        binding.readModeEditImageView2.setOnClickListener {
            startActivity(writeModeIntent)
        }

        val repo = MemoRepository(application)
        val viewModel = ViewModelProvider(this, ReadModeViewModel.Factory(repo)).get(ReadModeViewModel::class.java)

        lifecycleScope.launch{
            viewModel.state.collect{
                when(it){
                    is ReadModeState.Loading -> showLoadingImage()
                    is ReadModeState.SuccessToTakeMemo -> {
                        binding.readModeTextView4.text = it.memo
                        Timber.d("success: ${it.memo}")
                    }
                    is ReadModeState.SuccessToInsertMemo -> {}
                    is ReadModeState.FailedToTakeMemo -> binding.readModeTextView4.text = "작성된 메모가 없습니다."
                }
            }
        }
        Timber.d("readModeActivityTest: ${nonEditedDate}")
        viewModel.handleIntent(ReadModeIntent.TakeMemo(nonEditedDate))
    }
    fun showLoadingImage(){
    }
}