package com.enrico.story_app.presentation.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.enrico.story_app.R
import com.enrico.story_app.data.ResultState
import com.enrico.story_app.data.remote.request.LoginRequest
import com.enrico.story_app.databinding.ActivityLoginBinding
import com.enrico.story_app.presentation.ui.ViewModelFactory
import com.enrico.story_app.presentation.ui.home.HomeActivity
import com.enrico.story_app.presentation.ui.register.RegisterActivity
import com.enrico.story_app.utils.Constant
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val factory: ViewModelFactory = ViewModelFactory.getInstance()
    private val loginViewModel: LoginViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(Constant.preferencesName, Context.MODE_PRIVATE)

        binding.appLogo.transitionName = Constant.logoTransitionName
        binding.loginButton.setOnClickListener{
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            val loginRequest = LoginRequest(email, password)
            login(loginRequest)
        }

        binding.registerButton.setOnClickListener{
            val moveToRegister = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(moveToRegister)
        }
    }

    private fun login(loginRequest: LoginRequest) {
        loginViewModel.login(loginRequest).observe(this) { result ->
            if(result != null) {
                when (result){
                    is ResultState.Loading -> {
                        binding.loginButton.isEnabled = false
                        binding.registerButton.isEnabled = false
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is ResultState.Success -> {
                        goToHome(result.data.token)
                    }
                    is ResultState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.loginButton.isEnabled = true
                        binding.registerButton.isEnabled = true

                        Snackbar.make(binding.root, result.error, Snackbar.LENGTH_LONG)
                            .setBackgroundTint(ResourcesCompat.getColor(resources, R.color.colorError, theme))
                            .show()
                    }
                }
            }
        }
    }

    private fun goToHome(token: String){
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(Constant.preferencesToken, token)
        editor.apply()

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}