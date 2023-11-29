package com.dicoding.thestoryapp.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.thestoryapp.R
import com.dicoding.thestoryapp.constant.PREF_TOKEN
import com.dicoding.thestoryapp.databinding.ActivityLoginBinding
import com.dicoding.thestoryapp.ui.auth.viewmodel.AuthViewModel
import com.dicoding.thestoryapp.ui.story.ListStoryActivity
import com.dicoding.thestoryapp.util.Result
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var viewbinding: ActivityLoginBinding
    private val authLoginViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewbinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = viewbinding.root
        setContentView(view)

        hideActionBar()
        isLoading(false)

        playAnimation()

        viewbinding.loginEmail.addTextChangedListener(watcher())
        viewbinding.loginPassword.addTextChangedListener(watcher())

        viewbinding.register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        viewbinding.btnLogin.setOnClickListener{
            login()
        }

    }

    private fun hideActionBar() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun playAnimation() {
        with(viewbinding) {
            val titleLogin = ObjectAnimator.ofFloat(titleLogin, View.ALPHA, 1f).setDuration(500)
            val titleEmail = ObjectAnimator.ofFloat(titleEmail, View.ALPHA, 1f).setDuration(500)
            val edEmail = ObjectAnimator.ofFloat(loginEmail, View.ALPHA, 1f).setDuration(500)
            val titlePass = ObjectAnimator.ofFloat(titlePassword, View.ALPHA, 1f).setDuration(500)
            val edPass = ObjectAnimator.ofFloat(loginPassword, View.ALPHA, 1f).setDuration(500)
            val btnSignIn = ObjectAnimator.ofFloat(btnLogin, View.ALPHA, 1f).setDuration(500)
            val titleAtau = ObjectAnimator.ofFloat(Atau, View.ALPHA, 1f).setDuration(500)
            val titleRegister = ObjectAnimator.ofFloat(register, View.ALPHA, 1f).setDuration(500)

            ObjectAnimator.ofFloat(viewbinding.imgStory, View.TRANSLATION_X, -300f, 300f).apply {
                duration = 4000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

            AnimatorSet().apply {
                playSequentially(titleLogin, titleEmail, edEmail, titlePass, edPass, btnSignIn, titleAtau, titleRegister)
                start()
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun login() {
        val email = viewbinding.loginEmail.text.toString().trim()
        val password = viewbinding.loginPassword.text.toString().trim()

        authLoginViewModel.login(email, password).observe(this) { loginResult ->

            when(loginResult) {
                is Result.Loading -> {
                    isLoading(true)
                }
                is Result.Success -> {
                    isLoading(false)
                    val token = loginResult.data?.loginResult?.token
                    if (token.isNullOrBlank()) {
                        Toast.makeText(this@LoginActivity, resources.getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                        return@observe
                    }
                    PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
                        .edit()
                        .putString(PREF_TOKEN, token)
                        .apply()
                    val intent = Intent(this@LoginActivity, ListStoryActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                else -> {
                    isLoading(false)

                    if (loginResult.message.equals("User not found")) {
                        viewbinding.loginEmail.error = loginResult.message

                    } else if (loginResult.message.equals("Invalid password")) {
                        viewbinding.loginPassword.error = loginResult.message
                    }
                    Toast.makeText(this@LoginActivity, loginResult.message ?: resources.getString(R.string.login_failed), Toast.LENGTH_SHORT).show()

                }
            }

        }
    }

    private fun watcher() : TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewbinding.btnLogin.isEnabled =
                    viewbinding.loginEmail.text.toString().trim().isNotEmpty() &&
                            viewbinding.loginEmail.error == null &&
                            viewbinding.loginPassword.text.toString().trim().isNotEmpty() &&
                            viewbinding.loginPassword.error == null
            }

        }
    }

    private fun isLoading(isL: Boolean) {
        viewbinding.btnLogin.isEnabled = !isL
        if (isL) {
            viewbinding.rlLoading.visibility = View.VISIBLE
        } else {
            viewbinding.rlLoading.visibility = View.GONE
        }
    }
}