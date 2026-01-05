package com.univ.doraboda.ui

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment

abstract class BaseDialogFragment<T : ViewDataBinding>(): DialogFragment() {
    var _binding: T? = null
    val binding: T
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutInit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    abstract fun layoutId(): Int

    abstract fun layoutInit()

    fun DialogFragment.setDialogSize(wPercent: Float, hPercent: Float){
        var width: Int
        var height: Int
        if (Build.VERSION.SDK_INT >= 30) {
            val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val bounds = windowManager.currentWindowMetrics.bounds
            width = (bounds.width() * wPercent).toInt()
            height = (bounds.height() * hPercent).toInt()
        } else {
            val dm = Resources.getSystem().displayMetrics
            width = (dm.widthPixels * wPercent).toInt()
            height = (dm.heightPixels * hPercent).toInt()
        }
        dialog?.window?.setLayout(width, height)
    }
}