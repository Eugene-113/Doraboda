package com.univ.doraboda.ui

import android.app.AlertDialog
import android.os.Bundle
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.univ.doraboda.R
import com.univ.doraboda.databinding.ActivitySettingsBinding
import com.univ.doraboda.databinding.DialogLabelsBinding
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
    var flag = true
    val radioButtonList = listOf(R.id.labelRadioYellow, R.id.labelRadioLime, R.id.labelRadioPink, R.id.labelRadioSkyBlue)

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
                            if(flag){
                                labelType = it.labelType
                                firstLabelType = it.labelType //라벨타입 초기값 세팅
                                binding.settingsSwitch.isChecked = it.isQuoteModeOn //스위치 초기값 세팅
                                firstQuoteMode = it.isQuoteModeOn //스위치 초기값 세팅
                                flag = false
                            }
                        }
                    }
                }
            }
        }

        binding.settingsLayout1.setOnClickListener {
            val dialogBinding = DialogLabelsBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(this)
            builder.setView(dialogBinding.root)
            val dialog = builder.create()
            dialogBinding.labelRadioGroup.apply {
                check(radioButtonList.get(labelType)) //라디오버튼 초기값 세팅
            }
            dialogBinding.labelsCancelButton.setOnClickListener {
                dialog.dismiss()
            }
            dialogBinding.labelsDoneButton.setOnClickListener {
                val selectedButton = dialogBinding.root.findViewById<RadioButton>(dialogBinding.labelRadioGroup.checkedRadioButtonId)
                val index = dialogBinding.labelRadioGroup.indexOfChild(selectedButton)
                labelType = index
                dialog.dismiss()
            }
            dialog.show()
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