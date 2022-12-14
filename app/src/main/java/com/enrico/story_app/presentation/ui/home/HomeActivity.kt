package com.enrico.story_app.presentation.ui.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.enrico.story_app.R
import com.enrico.story_app.databinding.ActivityHomeBinding
import com.enrico.story_app.presentation.adapter.LoadingStateAdapter
import com.enrico.story_app.presentation.adapter.StoryAdapter
import com.enrico.story_app.presentation.ui.ViewModelFactory
import com.enrico.story_app.presentation.ui.add.AddActivity
import com.enrico.story_app.presentation.ui.login.LoginActivity
import com.enrico.story_app.presentation.ui.maps.MapsActivity
import com.enrico.story_app.utils.Constant
import com.google.android.material.snackbar.Snackbar

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var token: String? = null

    private lateinit var factory: ViewModelFactory
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

        factory = ViewModelFactory.getInstance(this)

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
                R.id.map -> {
                    goToMap()
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
        homeViewModel.getStories(token).observe(this) {
            binding.storyRv.setHasFixedSize(true)
            binding.storyRv.layoutManager = LinearLayoutManager(this)
            val adapter = StoryAdapter(this@HomeActivity)
            binding.storyRv.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )
            adapter.addLoadStateListener { loadState ->
                when (val currentState = loadState.refresh) {
                    is LoadState.Loading -> {
                        binding.storyRv.visibility = View.GONE
                        binding.shimmerLayout.visibility = View.VISIBLE
                    }
                    is LoadState.NotLoading  -> {
                        binding.storyRv.visibility = View.VISIBLE
                        binding.shimmerLayout.visibility = View.GONE
                    }
                    is LoadState.Error -> {
                        val extractedException = currentState.error // SomeCatchableException
                        binding.storyRv.visibility = View.GONE
                        binding.shimmerLayout.visibility = View.GONE
                        extractedException.message?.let { message ->
                            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
                                .setBackgroundTint(ResourcesCompat.getColor(resources, R.color.colorError, theme))
                                .show()
                        }
                    }
                }
            }
            homeViewModel.getStories(token).observe(this) {
                adapter.submitData(lifecycle, it)
            }
            binding.storyRv.visibility = View.VISIBLE
        }
    }

    private fun settingLanguage() {
        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }

    private fun goToMap() {
        val intent = Intent(this@HomeActivity, MapsActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.remove(Constant.preferencesToken)
        editor.apply()

        val intent = Intent(this@HomeActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}