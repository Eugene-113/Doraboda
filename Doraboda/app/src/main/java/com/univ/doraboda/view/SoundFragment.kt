package com.univ.doraboda.view

import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.univ.doraboda.R
import com.univ.doraboda.Service.SoundService
import com.univ.doraboda.SoundItem
import com.univ.doraboda.adapter.SoundAdapter
import com.univ.doraboda.databinding.FragmentSoundBinding

class SoundFragment : Fragment() {

    lateinit var soundAdapter: SoundAdapter
    lateinit var binding: FragmentSoundBinding
    var controllerFuture: ListenableFuture<MediaController>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSoundBinding.inflate(inflater, container, false)

        val list = listOf(SoundItem("rain", R.raw.sleepy_rain, "슬픈 선율", R.drawable.rain), SoundItem("birds", R.raw.birds, "부드러운 선율", R.drawable.sunflower))
        val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        soundAdapter = SoundAdapter()
        binding.soundRecyclerView.apply {
            layoutManager = manager
            adapter = soundAdapter
        }
        soundAdapter.submitList(list)

        return binding.root
    }

    @UnstableApi
    override fun onStart() {
        super.onStart()
        val appContext = requireContext().applicationContext
        val sessionToken = SessionToken(appContext,
            ComponentName(appContext, SoundService::class.java))

        controllerFuture = MediaController.Builder(appContext, sessionToken).buildAsync()
        controllerFuture!!.addListener({ soundAdapter.controller = controllerFuture!!.get() }, MoreExecutors.directExecutor())
    }

    override fun onDestroy() {
        MediaController.releaseFuture(controllerFuture!!)
        controllerFuture = null
        soundAdapter.controller = null
        super.onDestroy()
    }
}