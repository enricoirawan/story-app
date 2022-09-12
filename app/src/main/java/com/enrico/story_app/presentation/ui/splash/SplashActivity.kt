package com.enrico.story_app.presentation.ui.splash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.enrico.story_app.databinding.ActivitySplashBinding
import com.enrico.story_app.presentation.ui.home.HomeActivity
import com.enrico.story_app.presentation.ui.login.LoginActivity
import com.enrico.story_app.utils.Constant

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(Constant.preferencesName, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString(Constant.preferencesToken, "")!!

        if(token.isNotEmpty()){
            goToHome()

        }else {
            goToLogin()
        }
    }

    private fun goToLogin(){
        Handler(Looper.getMainLooper()).postDelayed({
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    Pair(binding.appLogo, Constant.logoTransitionName)
                )

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent, optionsCompat.toBundle())
            finish()
        }, Constant.delay)
    }

    private fun goToHome(){
        Handler(Looper.getMainLooper()).postDelayed({
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    Pair(binding.appLogo, Constant.logoTransitionName)
                )

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent, optionsCompat.toBundle())
            finish()
        }, 2000)
    }
}