package com.univ.doraboda.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.univ.doraboda.R
import com.univ.doraboda.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.homenavfragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.homeBottomNavigation.selectedItemId = R.id.calendarItem
        binding.homeBottomNavigation.setOnItemSelectedListener {item ->
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homenav_graph, true) //그래프 루트까지 제거
                .build()
            navController.navigate(item.itemId, null, navOptions)
            true
        }
    }
}