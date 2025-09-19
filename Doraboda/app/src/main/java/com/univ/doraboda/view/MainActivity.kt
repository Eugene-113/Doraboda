package com.univ.doraboda.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.univ.doraboda.R
import com.univ.doraboda.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
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
         */

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.homenavfragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.homeBottomNavigation.setupWithNavController(navController)
    }
}