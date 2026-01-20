package com.univ.doraboda.app.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.RadioButton
import androidx.core.os.bundleOf
import com.univ.doraboda.R
import com.univ.doraboda.app.base.BaseDialogFragment
import com.univ.doraboda.databinding.FragmentLabelDialogBinding

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