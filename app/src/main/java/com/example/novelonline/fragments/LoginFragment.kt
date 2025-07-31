package com.example.novelonline.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mobilecw.repository.UserRepository
import com.example.novelonline.R

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val emailEditText = view.findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = view.findViewById<EditText>(R.id.editTextPassword)
        val loginButton = view.findViewById<Button>(R.id.buttonLogin)
        val signUpText = view.findViewById<TextView>(R.id.textViewSignUp)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            when {
                email.isEmpty() -> showError(emailEditText, "Email is required")
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> showError(emailEditText, "Invalid email format")
                password.isEmpty() -> showError(passwordEditText, "Password is required")
                password.length < 6 -> showError(passwordEditText, "Password too short (min 6 characters)")
                else -> attemptLogin(email, password)
            }
        }

        signUpText.setOnClickListener {
            safeNavigateToSignUp()
        }

        return view
    }

    private fun safeNavigateToSignUp() {
        try {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SignUpFragment())
                .addToBackStack("loginToSignUp")
                .commit()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Please try again", Toast.LENGTH_SHORT).show()
        }
    }

    private fun attemptLogin(email: String, password: String) {
        UserRepository.login(email, password) { success, role, error ->
            if (success) {
                navigateToHome(role!!)
            } else {
                Toast.makeText(requireContext(), "Login failed: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToHome(role: String) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomeFragment().apply {
                arguments = Bundle().apply { putString("user_role", role) }
            })
            .commit()
    }

    private fun showError(field: EditText, message: String) {
        field.error = message
        field.requestFocus()
    }
}
