package com.univ.doraboda.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import com.univ.doraboda.databinding.ActivityWriteModeBinding

class WriteModeActivity : AppCompatActivity() {
    lateinit var binding: ActivityWriteModeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteModeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        binding.writeModeEditText.text = Editable.Factory.getInstance().newEditable(intent.getStringExtra("ETMemo").toString())
        binding.writeModeDeleteImageView.setOnClickListener {
            val intent = Intent()
            intent.putExtra("Btn", "delete")
            setResult(RESULT_OK, intent)
            finish()
        }
        binding.writeModeQuitImageView.setOnClickListener { finish() }
        binding.writeModeSaveImageView.setOnClickListener {
            val intent = Intent()
            intent.putExtra("Btn", "save")
            intent.putExtra("Memo", binding.writeModeEditText.text.toString())
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}