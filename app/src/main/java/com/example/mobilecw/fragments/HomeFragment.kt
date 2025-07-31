package com.example.mobilecw.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mobilecw.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val welcomeText = view.findViewById<TextView>(R.id.textViewWelcome)
        val roleText = view.findViewById<TextView>(R.id.textViewRole)
        val logoutButton = view.findViewById<Button>(R.id.buttonLogout)

        val role = arguments?.getString("user_role") ?: "guest"

        welcomeText.text = when (role) {
            "admin" -> "Welcome Admin!"
            "reader" -> "Welcome Reader!"
            "writer" -> "Welcome Writer!"
            else -> "Welcome Guest!"
        }

        roleText.text = "Your role: ${role.capitalize()}"

        logoutButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LoginFragment())
                .commit()
        }

        return view
    }
}

