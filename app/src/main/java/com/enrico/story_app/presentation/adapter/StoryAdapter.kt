package com.enrico.story_app.presentation.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.enrico.story_app.R
import com.enrico.story_app.data.remote.response.Story
import com.enrico.story_app.databinding.StoryItemBinding
import com.enrico.story_app.presentation.ui.detail.DetailActivity
import com.enrico.story_app.utils.Constant
import com.enrico.story_app.utils.withDateFormat

class StoryAdapter(private val storyList: List<Story>, private val activity: Activity) :
    RecyclerView.Adapter<StoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = storyList[position]

        holder.binding.nameTv.text = story.name
        holder.binding.descriptionTv.text = story.description
        holder.binding.dateTv.text =
            holder.itemView.context.getString(R.string.dateFormat, story.createdAt.withDateFormat())

        Glide.with(holder.itemView.context)
            .load(storyList[position].photoUrl)
            .into(holder.binding.storyIv)

        holder.itemView.setOnClickListener {
            val moveToDetailIntent = Intent(holder.itemView.context, DetailActivity::class.java)
            moveToDetailIntent.putExtra(DetailActivity.EXTRA_NAME, story.name)
            moveToDetailIntent.putExtra(DetailActivity.EXTRA_PHOTO_URL, story.photoUrl)
            moveToDetailIntent.putExtra(DetailActivity.EXTRA_DESCRIPTION, story.description)
            moveToDetailIntent.putExtra(DetailActivity.EXTRA_CREATED_AT, story.createdAt)

            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    Pair(holder.binding.storyIv, Constant.photoTransitionName)
                )

            holder.itemView.context.startActivity(moveToDetailIntent, optionsCompat.toBundle())
        }
    }

    override fun getItemCount(): Int = storyList.size

    class ViewHolder(var binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root)
}