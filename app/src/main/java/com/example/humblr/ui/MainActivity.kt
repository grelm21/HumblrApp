package com.example.humblr.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.humblr.R
import com.example.humblr.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        navView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        val navController = Navigation.findNavController(this, R.id.main_nav_host_fragment)

        NavigationUI.setupWithNavController(navView, navController)

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_nav_host_fragment, FeedFragment()).commit()


        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.feedFragment -> {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(androidx.navigation.ui.R.anim.nav_default_enter_anim, androidx.navigation.ui.R.anim.nav_default_exit_anim)
                        .replace(R.id.main_nav_host_fragment, FeedFragment()).commit()
                    true
                }
                R.id.favoriteFragment -> {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(androidx.navigation.ui.R.anim.nav_default_enter_anim, androidx.navigation.ui.R.anim.nav_default_exit_anim)
                        .replace(R.id.main_nav_host_fragment, FavoriteFragment()).commit()
                    true
                }
                R.id.profileFragment -> {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(androidx.navigation.ui.R.anim.nav_default_enter_anim, androidx.navigation.ui.R.anim.nav_default_exit_anim)
                        .replace(R.id.main_nav_host_fragment, ProfileFragment()).commit()
                    true
                }
                else -> {
                    true
                }
            }
        }
    }
}