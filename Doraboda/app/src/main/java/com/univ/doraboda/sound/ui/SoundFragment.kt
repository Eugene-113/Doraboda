package com.univ.doraboda.sound.ui

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.univ.doraboda.R
import com.univ.doraboda.databinding.FragmentSoundBinding
import com.univ.doraboda.sound.model.SoundItem
import com.univ.doraboda.sound.viewmodel.SoundViewModel
import com.univ.doraboda.app.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SoundFragment : BaseFragment<FragmentSoundBinding>() {
    var flag = true
    lateinit var soundAdapter: SoundAdapter
    val viewModel: SoundViewModel by viewModels()

    override fun layoutId(): Int = R.layout.fragment_sound

    override fun layoutInit(){
        val list = listOf(
            SoundItem("rain", R.raw.sleepy_rain, "슬픈 선율", R.drawable.rain, 0),
            SoundItem("birds", R.raw.birds, "부드러운 선율", R.drawable.sunflower, 1)
        )
        val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.state.collect {
                    if(it.isNetworkError){
                        Timber.Forest.d("명언 데이터 가져오지 않았거나 문제가 생김")
                    } else{
                        if(it.isLoading){
                        } else {
                            if(it.quote != "" && flag){
                                binding.readModeTextView3.text = "\"${it.quote}\" -${it.author}-"
                                binding.soundCardView2.visibility = View.VISIBLE
                                flag = false
                            }
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.errorEvents.collect{
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        if(isNetworkAvailable()) viewModel.handleIntent(SoundViewModel.SoundIntent.GetQuote)

        soundAdapter = SoundAdapter { item ->
            viewModel.handleIntent(SoundViewModel.SoundIntent.SeekAndPlayMusic(item.position))
        }
        binding.soundRecyclerView.apply {
            layoutManager = manager
            adapter = soundAdapter
        }
        soundAdapter.submitList(list)
    }

    fun isNetworkAvailable(): Boolean{
        //네트워크 상태 읽기
        val connectivityManager = requireContext().getSystemService(ConnectivityManager::class.java)
        val network = connectivityManager.activeNetwork ?: return false
        val actNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNetwork.hasTransport(
            NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    override fun onDestroy() {
        viewModel.handleIntent(SoundViewModel.SoundIntent.ReleaseIfConnected)
        super.onDestroy()
    }
}