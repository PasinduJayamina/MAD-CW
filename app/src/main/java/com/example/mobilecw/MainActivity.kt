package com.example.mobilecw

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mobilecw.fragments.LoginFragment
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase in your app
        FirebaseApp.initializeApp(this)

        // Set the activity layout that contains your fragment container
        setContentView(R.layout.activity_main)

        // Load the LoginFragment on first launch
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LoginFragment())
                .commit()
        }
    }
}
