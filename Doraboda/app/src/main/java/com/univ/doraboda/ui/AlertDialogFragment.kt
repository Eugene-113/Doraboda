package com.univ.doraboda.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.univ.doraboda.R
import com.univ.doraboda.databinding.FragmentAlertDialogBinding

class AlertDialogFragment : BaseDialogFragment<FragmentAlertDialogBinding>() {
    override fun layoutId(): Int = R.layout.fragment_alert_dialog
    override fun layoutInit() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.alertCancelButton.setOnClickListener {
            dismiss()
        }
        binding.alertDoneButton.setOnClickListener {
            parentFragmentManager.setFragmentResult("alertResult",
                bundleOf("answer" to true)
            )
            dismiss()
        }
    }
    override fun onStart() {
        super.onStart()
        setDialogSize(0.9f, 0.5f)
    }
}