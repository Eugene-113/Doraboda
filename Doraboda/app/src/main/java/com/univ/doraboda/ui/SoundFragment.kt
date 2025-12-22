package com.univ.doraboda.ui

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.univ.doraboda.R
import com.univ.doraboda.model.SoundItem
import com.univ.doraboda.adapter.SoundAdapter
import com.univ.doraboda.databinding.FragmentSoundBinding
import com.univ.doraboda.intent.SoundIntent
import com.univ.doraboda.viewModel.NetworkViewModel
import com.univ.doraboda.viewModel.SoundViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.getValue

@AndroidEntryPoint
class SoundFragment : BaseFragment<FragmentSoundBinding>() {

    lateinit var soundAdapter: SoundAdapter
    val soundViewModel: SoundViewModel by viewModels()
    val quoteViewModel: NetworkViewModel by viewModels()

    override fun layoutId(): Int = R.layout.fragment_sound

    override fun layoutInit(){
        val list = listOf(SoundItem("rain", R.raw.sleepy_rain, "슬픈 선율", R.drawable.rain, 0), SoundItem("birds", R.raw.birds, "부드러운 선율", R.drawable.sunflower, 1))
        val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        lifecycleScope.launch {
            quoteViewModel.state.collect {
                if(it.isError){
                    Timber.d("명언 데이터 가져오지 않았거나 문제가 생김")
                } else {
                    if(it.isLoading){
                    } else {
                        binding.readModeTextView3.text = "\"${it.quote}\" -${it.author}-"
                        binding.soundCardView2.visibility = View.VISIBLE
                    }
                }
            }
        }
        if (isNetworkAvailable()) quoteViewModel.handleIntent(NetworkViewModel.NetworkIntent.GetQuote)

        soundAdapter = SoundAdapter{ item ->
            soundViewModel.handleIntent(SoundIntent.SeekAndPlayMusic(item.position))
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
        return actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    override fun onDestroy() {
        soundViewModel.handleIntent(SoundIntent.ReleaseIfConnected)
        super.onDestroy()
    }
}