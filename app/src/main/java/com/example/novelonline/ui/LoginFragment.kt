package com.example.novelonline.ui

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.novelonline.R
import com.example.novelonline.databinding.FragmentLoginBinding
import com.example.novelonline.repository.UserRepository

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString()

            when {
                email.isEmpty() -> showError(binding.editTextEmail, "Email is required")
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> showError(binding.editTextEmail, "Invalid email format")
                password.isEmpty() -> showError(binding.editTextPassword, "Password is required")
                password.length < 6 -> showError(binding.editTextPassword, "Password too short (min 6 characters)")
                else -> attemptLogin(email, password)
            }
        }

        binding.textViewSignUp.setOnClickListener {
            // Use the Navigation Component to go to the SignUpFragment
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun attemptLogin(email: String, password: String) {
        UserRepository.login(email, password) { success, role, error ->
            if (success && role != null) {
                // Use the Navigation Component to go to the HomeFragment
                val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), "Login failed: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showError(field: EditText, message: String) {
        field.error = message
        field.requestFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}