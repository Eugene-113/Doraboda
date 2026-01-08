package com.univ.doraboda.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.os.bundleOf
import com.univ.doraboda.R
import com.univ.doraboda.databinding.FragmentFileQuestBinding

class FileQuestDialogFragment: BaseDialogFragment<FragmentFileQuestBinding>() {
    override fun layoutId() = R.layout.fragment_file_quest
    override fun layoutInit() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.fileQuestCancelButton.setOnClickListener {
            dismiss()
        }
        binding.fileQuestDoneButton.setOnClickListener {
            parentFragmentManager.setFragmentResult("fileQuestResult",
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