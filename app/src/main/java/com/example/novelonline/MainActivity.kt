package com.example.novelonline

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        // This line is from the hotfix branch to enable a full-screen UI
        enableEdgeToEdge()

        // This is your code from feature/home to set up the layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // This is the listener code from hotfix, adapted to use View Binding.
        // It adds padding to prevent your UI from overlapping with the system bars.
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // This is your navigation setup code from feature/home
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        binding.bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.libraryFragment || item.itemId == R.id.profileFragment) {
                Toast.makeText(this, "${item.title} is not available yet", Toast.LENGTH_SHORT).show()
                return@setOnItemSelectedListener false
            }
            NavigationUI.onNavDestinationSelected(item, navController)
            return@setOnItemSelectedListener true
        }
    }
}