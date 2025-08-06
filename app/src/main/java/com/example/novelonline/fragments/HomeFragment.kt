package com.example.novelonline.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.novelonline.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val role = arguments?.getString("user_role") ?: "guest"


        if (role.lowercase() == "admin") {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AdminDashboardFragment())
                .commit()
            return null
        }


        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val welcomeText = view.findViewById<TextView>(R.id.textViewWelcome)
        val roleText = view.findViewById<TextView>(R.id.textViewRole)
        val logoutButton = view.findViewById<Button>(R.id.buttonLogout)

        welcomeText.text = if (role.lowercase() == "user") "Welcome User!" else ""
        roleText.text = "Your role: ${role.replaceFirstChar { it.uppercase() }}"

        logoutButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LoginFragment())
                .commit()
        }

        return view
    }
}
