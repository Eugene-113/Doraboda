package com.univ.doraboda.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.univ.doraboda.databinding.ActivitySettingsBinding
import com.univ.doraboda.viewModel.SettingsViewModel
import com.univ.doraboda.viewModel.SettingsViewModel.SettingsIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.getValue

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    val viewModel: SettingsViewModel by viewModels()
    lateinit var binding: ActivitySettingsBinding
    var labelType = 0 //현재 라벨타입
    var firstLabelType = 0 //최초 라벨타입
    var firstQuoteMode = true //최초 명언모드
    var initFlag = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.state.collect {
                    if(it.isError){
                        Timber.d("error in settings")
                    }
                    else{
                        if(it.isQuoteModeOn != null && it.labelType != -1){
                            if(initFlag){
                                labelType = it.labelType
                                firstLabelType = it.labelType //라벨타입 초기값 세팅
                                binding.settingsSwitch.isChecked = it.isQuoteModeOn //스위치 초기값 세팅
                                firstQuoteMode = it.isQuoteModeOn //스위치 초기값 세팅
                                initFlag = false
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.errorEvents.collect {
                    Toast.makeText(this@SettingsActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
        supportFragmentManager.setFragmentResultListener("settingsResult", this){ _, bundle ->
            labelType = bundle.getInt("dialogIndex")
        }
        binding.settingsLayout1.setOnClickListener {
            if(!initFlag){ //labelType 초기값세팅 완료될시 실행
                LabelDialogFragment().apply {
                    arguments = bundleOf("fragmentIndex" to labelType)
                }.show(supportFragmentManager, "dialog")
            }
        }
    }

    override fun onDestroy() {
        if(labelType != firstLabelType){ //labelType 바꼈으면
            viewModel.handleIntent(SettingsIntent.SetLabelType(labelType))
        }
        if(firstQuoteMode != binding.settingsSwitch.isChecked){
            viewModel.handleIntent(SettingsIntent.SetQuoteMode(binding.settingsSwitch.isChecked))
        }
        super.onDestroy()
    }
}