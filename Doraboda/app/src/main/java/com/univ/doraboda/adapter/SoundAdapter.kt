package com.univ.doraboda.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.media3.session.MediaController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.univ.doraboda.SoundItem
import com.univ.doraboda.databinding.ItemSoundBinding

class SoundAdapter : ListAdapter<SoundItem, SoundAdapter.SoundViewHolder>(EmotionDiffCallback) {
    var isPlay = false
    var playIndex = 0
    var controller: MediaController? = null

    object EmotionDiffCallback : DiffUtil.ItemCallback<SoundItem>(){
        override fun areItemsTheSame(oldItem: SoundItem, newItem: SoundItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: SoundItem, newItem: SoundItem): Boolean {
            return oldItem.music == newItem.music
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SoundViewHolder {
        val binding = ItemSoundBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SoundViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SoundViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SoundViewHolder(val binding: ItemSoundBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: SoundItem){
            binding.soundItemDesc.text = item.desc
            binding.soundItemLayout.setBackgroundResource(item.image)
            binding.soundItemImageView.setOnClickListener {
                if(isPlay){ //노래가 나오는 중이다: 수정 예정.
                    if(controller != null){
                        if(playIndex == absoluteAdapterPosition){ //1: 현재 나오고 있는 노래를 선택하면? 노래중지
                            controller!!.pause()
                            isPlay = false
                        } else{ //2: 현재 나오지 않는 노래를 선택하면? 노래변경
                            controller!!.seekTo(absoluteAdapterPosition, 0)
                        }
                    }
                    //노래를 중지시킨다, 노래는 몇개를 동시에 틀 수 없다
                }
                else { //아무 노래도 나오지 않고 있다
                    //평범하게 재생
                    if(controller != null){
                        controller!!.seekTo(absoluteAdapterPosition, 0)
                        controller!!.play()
                        playIndex = absoluteAdapterPosition
                        isPlay = true
                    }
                }
            }
        }
    }
}