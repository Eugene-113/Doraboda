package com.univ.doraboda.calendar.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import com.univ.doraboda.databinding.ActivityWriteModeBinding
import com.univ.doraboda.app.dialog.AlertDialogFragment

class WriteModeActivity : AppCompatActivity() {
    lateinit var binding: ActivityWriteModeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteModeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.setFragmentResultListener("alertResult", this) { _, bundle ->
            if(!bundle.getBoolean("answer")) return@setFragmentResultListener
            val intent = Intent()
            intent.putExtra("Mode", "memo")
            intent.putExtra("Btn", "delete")
            setResult(RESULT_OK, intent)
            finish()
        }
        val intent = intent
        binding.writeModeEditText.text = Editable.Factory.getInstance().newEditable(intent.getStringExtra("ETMemo").toString())
        binding.writeModeDeleteImageView.setOnClickListener {
            AlertDialogFragment().show(supportFragmentManager, "dialog")
        }
        binding.writeModeQuitImageView.setOnClickListener { finish() }
        binding.writeModeSaveImageView.setOnClickListener {
            val intent = Intent()
            intent.putExtra("Mode", "memo")
            intent.putExtra("Btn", "save")
            intent.putExtra("Memo", binding.writeModeEditText.text.toString())
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}