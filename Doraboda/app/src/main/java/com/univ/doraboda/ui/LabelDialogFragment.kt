package com.univ.doraboda.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.univ.doraboda.R
import com.univ.doraboda.databinding.FragmentLabelDialogBinding
import timber.log.Timber

class LabelDialogFragment : BaseDialogFragment<FragmentLabelDialogBinding>() {
    val radioButtonList = listOf(R.id.labelRadioYellow, R.id.labelRadioLime, R.id.labelRadioPink, R.id.labelRadioSkyBlue)

    override fun layoutId() = R.layout.fragment_label_dialog

    override fun layoutInit() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.labelRadioPink.post {
            binding.labelRadioPink.layoutParams.height = binding.labelRadioPink.width
            binding.labelRadioPink.requestLayout()
        }
        binding.labelRadioLime.post {
            binding.labelRadioLime.layoutParams.height = binding.labelRadioPink.width
            binding.labelRadioLime.requestLayout()
        }
        binding.labelRadioSkyBlue.post {
            binding.labelRadioSkyBlue.layoutParams.height = binding.labelRadioPink.width
            binding.labelRadioSkyBlue.requestLayout()
        }
        binding.labelRadioYellow.post {
            binding.labelRadioYellow.layoutParams.height = binding.labelRadioPink.width
            binding.labelRadioYellow.requestLayout()
        }
        val fragmentIndex = arguments?.getInt("fragmentIndex")
        binding.labelRadioGroup.check(radioButtonList.get(fragmentIndex ?: 0))
        binding.labelsCancelButton.setOnClickListener {
            dismiss()
        }
        binding.labelsDoneButton.setOnClickListener {
            val selectedButton = binding.root.findViewById<RadioButton>(binding.labelRadioGroup.checkedRadioButtonId)
            val index = binding.labelRadioGroup.indexOfChild(selectedButton)
            parentFragmentManager.setFragmentResult("settingsResult",
                bundleOf("dialogIndex" to index)
            )
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        setDialogSize(0.9f, 0.5f)
    }
}