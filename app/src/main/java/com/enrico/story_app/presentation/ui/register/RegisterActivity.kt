package com.enrico.story_app.presentation.ui.register

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.enrico.story_app.R
import com.enrico.story_app.data.ResultState
import com.enrico.story_app.data.remote.request.RegisterRequest
import com.enrico.story_app.databinding.ActivityRegisterBinding
import com.enrico.story_app.presentation.ui.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var factory: ViewModelFactory
    private val registerViewModel: RegisterViewModel by viewModels {
        factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(this)

        binding.registerButton.setOnClickListener{
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            val registerRequest = RegisterRequest(name, email, password)
            register(registerRequest)
        }

        binding.loginButton.setOnClickListener{
           finish()
        }
    }

    private fun register(registerRequest: RegisterRequest) {
        registerViewModel.register(registerRequest).observe(this) { result ->
            if(result != null) {
                when (result){
                    is ResultState.Loading -> {
                        binding.loginButton.isEnabled = false
                        binding.registerButton.isEnabled = false
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is ResultState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.root, result.data.message, Snackbar.LENGTH_LONG)
                            .setBackgroundTint(ResourcesCompat.getColor(resources, R.color.colorPrimary, theme))
                            .show()

                        Handler(Looper.getMainLooper()).postDelayed({
                            finish()
                        }, 2000)
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
}