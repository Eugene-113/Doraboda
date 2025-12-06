package com.univ.doraboda.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.univ.doraboda.R
import com.univ.doraboda.SoundItem
import com.univ.doraboda.adapter.SoundAdapter
import com.univ.doraboda.databinding.FragmentSoundBinding
import com.univ.doraboda.intent.SoundIntent
import com.univ.doraboda.viewModel.SoundViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class SoundFragment : Fragment() {

    lateinit var soundAdapter: SoundAdapter
    lateinit var binding: FragmentSoundBinding
    val viewModel: SoundViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSoundBinding.inflate(inflater, container, false)

        val list = listOf(SoundItem("rain", R.raw.sleepy_rain, "슬픈 선율", R.drawable.rain, 0), SoundItem("birds", R.raw.birds, "부드러운 선율", R.drawable.sunflower, 1))
        val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        soundAdapter = SoundAdapter{ item ->
            viewModel.handleIntent(SoundIntent.SeekAndPlayMusic(item.position))
        }
        binding.soundRecyclerView.apply {
            layoutManager = manager
            adapter = soundAdapter
        }
        soundAdapter.submitList(list)

        return binding.root
    }

    override fun onDestroy() {
        viewModel.handleIntent(SoundIntent.ReleaseIfConnected)
        super.onDestroy()
    }
}