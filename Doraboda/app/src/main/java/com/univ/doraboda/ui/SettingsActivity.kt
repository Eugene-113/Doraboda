package com.univ.doraboda.ui

import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import kotlin.getValue

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    val viewModel: SettingsViewModel by viewModels()
    lateinit var binding: ActivitySettingsBinding
    var labelType = 0 //현재 라벨타입
    var initFlag = true

    val startForResult2: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if(result.resultCode == RESULT_OK){
            val uri = result.data?.data ?: return@registerForActivityResult
            viewModel.handleIntent(SettingsIntent.LoadAllDoraData(uri))
        }
    }

    val startForResult1: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if(result.resultCode == RESULT_OK){
            val uri = result.data?.data ?: return@registerForActivityResult
            viewModel.handleIntent(SettingsIntent.SaveAllDoraData(uri))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.setFragmentResultListener("fileQuestResult", this) { _, bundle ->
            if(!bundle.getBoolean("answer")) return@setFragmentResultListener
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                setType("application/json")
            }
            startForResult2.launch(intent)
        }

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.state.collect {
                    if(it.isQuoteModeOn != null){
                        if(it.loadNew){
                            labelType = it.labelType
                            binding.settingsSwitch.isChecked = it.isQuoteModeOn
                        }
                        if(initFlag){
                            labelType = it.labelType
                            binding.settingsSwitch.isChecked = it.isQuoteModeOn //스위치 초기값 세팅
                            initFlag = false
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
            viewModel.handleIntent(SettingsIntent.SetLabelType(labelType))
        }
        binding.settingsLayout1.setOnClickListener {
            if(!initFlag){ //labelType 초기값세팅 완료될시 실행
                LabelDialogFragment().apply {
                    arguments = bundleOf("fragmentIndex" to labelType)
                }.show(supportFragmentManager, "dialog")
            }
        }
        binding.settingsLayout2.setOnClickListener {
            readFolder()
        }
        binding.settingsLayout3.setOnClickListener {
            FileQuestDialogFragment().show(supportFragmentManager, "dialog")
        }
        binding.settingsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.handleIntent(SettingsIntent.SetQuoteMode(isChecked))
        }
        binding.settingsTextView9.setOnClickListener {
            val url = "https://github.com/Eugene-113/Doraboda"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
        viewModel.handleIntent(SettingsIntent.LoadAllFirst)
    }

    fun readFolder(){
        val calendar = Calendar.getInstance()
        val dateString = "${calendar.get(Calendar.YEAR)}/${calendar.get(Calendar.MONTH)+1}/${calendar.get(Calendar.DAY_OF_MONTH)}"
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            setType("application/json")
            putExtra(Intent.EXTRA_TITLE, "DoraBoda - ${dateString}.json")
        }
        startForResult1.launch(intent)
    }
}