package com.univ.doraboda.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.univ.doraboda.model.SoundItem
import com.univ.doraboda.databinding.ItemSoundBinding

class SoundAdapter(val onClick: (SoundItem) -> Unit) : ListAdapter<SoundItem, SoundAdapter.SoundViewHolder>(EmotionDiffCallback) {

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
        fun bind(item: SoundItem){ //원하는동작: 누르면 그게 이미 재생되든 말든 그거재생
            binding.soundItemDesc.text = item.desc
            binding.soundItemLayout.setBackgroundResource(item.image)
            binding.soundItemImageView.setOnClickListener {
                onClick(item)
            }
        }
    }
}