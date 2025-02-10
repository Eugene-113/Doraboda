package com.univ.doraboda.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.univ.doraboda.R
import com.univ.doraboda.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val soundFragment = SoundFragment()
        val calendarFragment = CalendarFragment()
        val dataFragment = DataFragment()

        binding.homeBottomNavigation.setOnItemSelectedListener {item ->
            val fragment = when (item.itemId) {
                R.id.soundItem -> {
                    soundFragment
                }
                R.id.calendarItem -> {
                    calendarFragment

                }
                else -> { dataFragment }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.homeFrameLayout, fragment)
                .commit()
            true
        }
        binding.homeBottomNavigation.selectedItemId = R.id.calendarItem
    }
}