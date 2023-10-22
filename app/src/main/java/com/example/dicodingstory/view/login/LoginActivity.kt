package com.example.dicodingstory.view.login

import com.example.dicodingstory.view.main.MainActivity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dicodingstory.ViewModelFactory
import com.example.dicodingstory.data.ResultState
import com.example.dicodingstory.databinding.ActivityLoginBinding
import com.example.dicodingstory.view.signup.SignupActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
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

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                showErrorDialog("Email dan Password harus diisi.")
                return@setOnClickListener
            }

            binding.progressBarSigin.visibility = View.VISIBLE
            lifecycleScope.launch {

                when (val result = viewModel.login(email, password)) {
                    is ResultState.Success -> {
                        val userModel = result.data
                        binding.progressBarSigin.visibility = View.GONE
                        viewModel.saveSession(userModel)
                        AlertDialog.Builder(this@LoginActivity).apply {
                            setTitle("Yeah!")
                            setMessage("Anda berhasil login....")
                            setPositiveButton("Lanjut") { _, _ ->
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    }

                    is ResultState.Error -> {
                        binding.progressBarSigin.visibility = View.GONE
                        AlertDialog.Builder(this@LoginActivity).apply {
                            setTitle("Oops!")
                            setMessage("Login gagal, Periksa email dan kata sandi Anda.")
                            setPositiveButton("Tutup") { dialog, _ ->
                                dialog.dismiss()
                            }
                            create()
                            show()
                        }
                    }
                    else -> {
                    }
                }
            }
        }

        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this@LoginActivity).apply {
            setTitle("Oops!")
            setMessage(message)
            setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }
}
