package com.example.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.databinding.ItemStoryBinding

class StoryAdapter(private val listStory: List<Story>, private val onClick: (Story) -> Unit) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvItemName.text = listStory[holder.adapterPosition].name
        Glide.with(holder.itemView)
            .load(listStory[holder.adapterPosition].photoUrl)
            .into(holder.binding.ivItemPhoto)
        holder.binding.tvItemDesc.text = listStory[holder.adapterPosition].description

        holder.itemView.setOnClickListener{
            onClick(listStory[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = listStory.size



}