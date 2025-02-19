package com.univ.doraboda

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val soundFragment = SoundFragment()
        val calendarFragment = CalendarFragment()
        val dataFragment = DataFragment()

        val homeSettingImageView: ImageView = findViewById(R.id.homeSettingsImageView)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.homeBottomNavigation)

        val intent = Intent(this, SettingsActivity::class.java)
        homeSettingImageView.setOnClickListener {
            startActivity(intent)
        }

        bottomNavigationView.setOnItemSelectedListener {item ->
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
        bottomNavigationView.selectedItemId = R.id.calendarItem
    }
}