package com.univ.doraboda

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.*

class CalendarFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}