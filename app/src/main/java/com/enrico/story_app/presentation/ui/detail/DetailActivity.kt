package com.enrico.story_app.presentation.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.enrico.story_app.R
import com.enrico.story_app.databinding.ActivityDetailBinding
import com.enrico.story_app.utils.Constant
import com.enrico.story_app.utils.withDateFormat

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.storyIv.transitionName = Constant.photoTransitionName

        val name = intent.getStringExtra(EXTRA_NAME)
        val description = intent.getStringExtra(EXTRA_DESCRIPTION)
        val photoUrl = intent.getStringExtra(EXTRA_PHOTO_URL)
        val createdAt = intent.getStringExtra(EXTRA_CREATED_AT)

        Glide.with(this@DetailActivity)
            .load(photoUrl)
            .into(binding.storyIv)

        binding.nameTv.text = name
        binding.descriptionTv.text = description
        binding.createdAtTv.text = getString(R.string.dateFormat, createdAt?.withDateFormat())

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val EXTRA_NAME = "EXTRA_NAME"
        const val EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION"
        const val EXTRA_PHOTO_URL = "EXTRA_PHOTO_URL"
        const val EXTRA_CREATED_AT = "EXTRA_CREATED_AT"
    }
}