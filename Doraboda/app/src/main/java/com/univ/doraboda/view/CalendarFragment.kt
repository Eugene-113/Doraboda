package com.univ.doraboda.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.univ.doraboda.util.CalendarUtil
import com.univ.doraboda.R
import com.univ.doraboda.adapter.CalendarAdapter
import com.univ.doraboda.databinding.FragmentCalendarBinding
import java.util.Calendar

class CalendarFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentCalendarBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false)
        val intent = Intent(activity, SettingsActivity::class.java)
        binding.calendarSettingsImageView.setOnClickListener {
            startActivity(intent)
        }
        val list = CalendarUtil().getDays(Calendar.getInstance())
        val manager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val calendarAdapter = CalendarAdapter()
        binding.calendarRecyclerView.apply {
            layoutManager = manager
            adapter = calendarAdapter
            scrollToPosition(1)
        }
        calendarAdapter.submitList(list)
        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(binding.calendarRecyclerView)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}