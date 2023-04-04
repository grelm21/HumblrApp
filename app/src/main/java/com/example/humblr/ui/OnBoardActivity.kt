package com.example.humblr.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.humblr.R
import com.example.humblr.databinding.ActivityOnBoardBinding
import com.example.humblr.utils.OnBoardState
import com.example.humblr.utils.OnSwipeTouchListener
import com.example.humblr.viewmodels.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnBoardActivity : AppCompatActivity() {

    private val _viewModel: LoginViewModel by viewModel()
    private lateinit var _binding: ActivityOnBoardBinding
    private var _state: OnBoardState = OnBoardState.One

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityOnBoardBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        lifecycleScope.launchWhenCreated {
            _viewModel.token.collect {
                if (!it.isNullOrEmpty()) {
                    openApp()
                }
//                else {
//                    openLogIn()
//                }
            }
        }

        _viewModel.getToken()

        _binding.skipNext.setOnClickListener {
            openLogIn()
        }

        _binding.image.setImageResource(R.drawable.onboard_one)
        _binding.progress.setImageResource(R.drawable.onboard_progress_one)
        _binding.tvDescription.text = resources.getString(R.string.onboard_one)
        _binding.skipNext.text = resources.getString(R.string.skip)

        _binding.root.setOnTouchListener(object : OnSwipeTouchListener(this@OnBoardActivity) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                when (_state) {
                    OnBoardState.One -> {
                        _binding.image.setImageResource(R.drawable.onboard_two)
                        _binding.progress.setImageResource(R.drawable.onboard_progress_two)
                        _binding.tvDescription.text = resources.getString(R.string.onboard_two)
                        _binding.skipNext.text = resources.getString(R.string.skip)
                        _state = OnBoardState.Two
                    }
                    OnBoardState.Two -> {
                        _binding.image.setImageResource(R.drawable.onboard_three)
                        _binding.progress.setImageResource(R.drawable.onboard_progress_three)
                        _binding.tvDescription.text = resources.getString(R.string.onboard_three)
                        _binding.skipNext.text = resources.getString(R.string.forward)
                        _state = OnBoardState.Three
                    }
                    OnBoardState.Three -> {

                    }
                }
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                when (_state) {
                    OnBoardState.One -> {

                    }
                    OnBoardState.Two -> {
                        _binding.image.setImageResource(R.drawable.onboard_one)
                        _binding.progress.setImageResource(R.drawable.onboard_progress_one)
                        _binding.tvDescription.text = resources.getString(R.string.onboard_one)
                        _binding.skipNext.text = resources.getString(R.string.skip)
                        _state = OnBoardState.One
                    }
                    OnBoardState.Three -> {
                        _binding.image.setImageResource(R.drawable.onboard_two)
                        _binding.progress.setImageResource(R.drawable.onboard_progress_two)
                        _binding.tvDescription.text = resources.getString(R.string.onboard_two)
                        _binding.skipNext.text = resources.getString(R.string.skip)
                        _state = OnBoardState.Two
                    }
                }
            }
        })
    }

    private fun openApp() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun openLogIn() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}