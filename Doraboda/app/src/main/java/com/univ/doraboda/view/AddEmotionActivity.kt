package com.univ.doraboda.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.univ.doraboda.databinding.ActivityAddEmotionBinding

class AddEmotionActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddEmotionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEmotionBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}