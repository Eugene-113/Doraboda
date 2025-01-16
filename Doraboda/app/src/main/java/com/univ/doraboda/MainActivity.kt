package com.univ.doraboda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Objects

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tabLayout: TabLayout = findViewById(R.id.homeTabLayout)
        val homeAdapter: ViewPager2 = findViewById(R.id.homeViewPager)
        homeAdapter.adapter = HomeViewPager2Adapter(this)

        TabLayoutMediator(
            tabLayout, homeAdapter
        ) { tab, position ->
            tab.text = when(position){
                0 -> "소리"
                1 -> "일기"
                else -> "설정"
            }
        }.attach()
    }
}