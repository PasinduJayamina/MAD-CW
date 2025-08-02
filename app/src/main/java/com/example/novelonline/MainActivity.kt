package com.example.novelonline

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.novelonline.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Find the NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up the BottomNavigationView with the NavController
        binding.bottomNav.setupWithNavController(navController)

        // Add a custom listener to disable certain tabs
        binding.bottomNav.setOnItemSelectedListener { item ->
            // Check if the selected item is one of the disabled tabs
            if (item.itemId == R.id.libraryFragment || item.itemId == R.id.profileFragment) {
                Toast.makeText(this, "${item.title} is not available yet", Toast.LENGTH_SHORT).show()
                return@setOnItemSelectedListener false // Prevents navigation
            }
            // For other items, allow the default navigation behavior
            NavigationUI.onNavDestinationSelected(item, navController)
            return@setOnItemSelectedListener true
        }
    }
}