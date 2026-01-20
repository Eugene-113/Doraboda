package com.univ.doraboda.app.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.os.bundleOf
import com.univ.doraboda.R
import com.univ.doraboda.app.base.BaseDialogFragment
import com.univ.doraboda.databinding.FragmentDateDialogBinding

class DateDialogFragment : BaseDialogFragment<FragmentDateDialogBinding>() {
    override fun layoutId(): Int = R.layout.fragment_date_dialog
    override fun layoutInit() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val fragmentDate = arguments?.getString("fragmentDate") ?: "2000/1"
        val dateArr = fragmentDate.split("/")
        binding.yearNumberPicker.apply {
            minValue = 2000
            maxValue = 3000
            value = dateArr.get(0).toInt()
        }
        binding.monthNumberPicker.apply {
            minValue = 1
            maxValue = 12
            value = dateArr.get(1).toInt()
        }
        binding.calendarDatePickerCancelButton.setOnClickListener {
            dismiss()
        }
        binding.calendarDataPickerDoneButton.setOnClickListener {
            parentFragmentManager.setFragmentResult("calendarResult",
                bundleOf("dialogDate" to "${binding.yearNumberPicker.value}/${binding.monthNumberPicker.value}")
            )
            dismiss()
        }
    }
    override fun onStart() {
        super.onStart()
        setDialogSize(0.9f, 0.5f)
    }
}