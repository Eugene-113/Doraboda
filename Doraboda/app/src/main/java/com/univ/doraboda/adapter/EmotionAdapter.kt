package com.univ.doraboda.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.univ.doraboda.EmotionItem
import com.univ.doraboda.databinding.ItemEmotionBinding
import timber.log.Timber

class EmotionAdapter(val context: Context, val emotion: String?) : ListAdapter<EmotionItem, EmotionAdapter.EmotionViewHolder>(EmotionDiffCallback) {
    var previousIndex = -1
    var thisIndex = 0
    var isSelectedFlag = false
    var thisEmotion: String? = "unchanged"

    object EmotionDiffCallback : DiffUtil.ItemCallback<EmotionItem>(){
        override fun areItemsTheSame(oldItem: EmotionItem, newItem: EmotionItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: EmotionItem, newItem: EmotionItem): Boolean {
            return oldItem.image == newItem.image
        }
    }

    inner class EmotionViewHolder(val context: Context, val binding: ItemEmotionBinding, val emotion: String?) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: EmotionItem){
            Glide.with(context).load(item.image).into(binding.emotionImageView)
            if(!isSelectedFlag && emotion == item.name){ //최초선택된게 없고 현재항목이 최초선택해야하는 항목일때,
                Timber.d("first ${absoluteAdapterPosition}")
                binding.emotionCardView.setCardBackgroundColor(Color.GRAY)
                previousIndex = absoluteAdapterPosition
                isSelectedFlag = true
            } else if(isSelectedFlag && previousIndex == absoluteAdapterPosition){ //최초선택 후, 현재항목이 선택해야하는 항목일때
                binding.emotionCardView.setCardBackgroundColor(Color.WHITE)
                previousIndex = thisIndex
            }
            binding.emotionImageView.setOnClickListener {
                if(previousIndex != absoluteAdapterPosition){ //전과 다른 item 클릭
                    thisEmotion = item.name
                    binding.emotionCardView.setCardBackgroundColor(Color.GRAY)
                    thisIndex = absoluteAdapterPosition
                    notifyItemChanged(previousIndex)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmotionViewHolder {
        val binding = ItemEmotionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmotionViewHolder(context, binding, emotion)
    }

    override fun onBindViewHolder(holder: EmotionViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}