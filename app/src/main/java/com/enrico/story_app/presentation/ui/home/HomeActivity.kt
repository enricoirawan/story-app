package com.enrico.story_app.presentation.ui.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.enrico.story_app.R
import com.enrico.story_app.data.ResultState
import com.enrico.story_app.data.remote.response.Story
import com.enrico.story_app.databinding.ActivityHomeBinding
import com.enrico.story_app.presentation.adapter.StoryAdapter
import com.enrico.story_app.presentation.ui.ViewModelFactory
import com.enrico.story_app.presentation.ui.add.AddActivity
import com.enrico.story_app.presentation.ui.login.LoginActivity
import com.enrico.story_app.utils.Constant
import com.google.android.material.snackbar.Snackbar

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var token: String? = null

    private val factory: ViewModelFactory = ViewModelFactory.getInstance()
    private val homeViewModel: HomeViewModel by viewModels {
        factory
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK){
                token?.let { it -> getStories(it) }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appBarLayout.transitionName = Constant.logoTransitionName

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    logout()
                    true
                }
                R.id.setting -> {
                    settingLanguage()
                    true
                }
                else -> false
            }
        }

        binding.addFab.setOnClickListener{
            val intent = Intent(this, AddActivity::class.java)
            getResult.launch(intent)
        }

        sharedPreferences = getSharedPreferences(Constant.preferencesName, Context.MODE_PRIVATE)
        token = sharedPreferences.getString(Constant.preferencesToken, "")

        token?.let {
            getStories(it)
        }
    }

    private fun getStories(token: String) {
        homeViewModel.getStories(token).observe(this) { result ->
            if (result != null) {
                when(result){
                    is ResultState.Loading -> {
                        binding.shimmerLayout.visibility = View.VISIBLE
                    }
                    is ResultState.Success -> {
                        binding.shimmerLayout.visibility = View.GONE
                        setUpStoryRecycler(result.data)
                    }
                    is ResultState.Error -> {
                        binding.shimmerLayout.visibility = View.GONE
                        Snackbar.make(binding.root, result.error, Snackbar.LENGTH_LONG)
                            .setBackgroundTint(ResourcesCompat.getColor(resources, R.color.colorError, theme))
                            .setAction(R.string.retry) {
                                homeViewModel.getStories(token!!)
                            }
                            .show()
                    }
                }
            }
        }
    }

    private fun setUpStoryRecycler(stories: List<Story>){
        binding.storyRv.setHasFixedSize(true)
        binding.storyRv.layoutManager = LinearLayoutManager(this)
        val storyAdapter = StoryAdapter(stories, this)
        binding.storyRv.adapter = storyAdapter
        binding.storyRv.visibility = View.VISIBLE
    }

    private fun settingLanguage() {
        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }

    private fun logout() {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.remove(Constant.preferencesToken)
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}