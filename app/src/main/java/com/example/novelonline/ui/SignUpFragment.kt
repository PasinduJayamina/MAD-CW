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
import com.example.novelonline.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.buttonSignUp.setOnClickListener {
            val firstName = binding.editTextFirstName.text.toString().trim()
            val lastName = binding.editTextLastName.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.text.toString()

            when {
                firstName.isEmpty() -> showError(binding.editTextFirstName, "First name required")
                lastName.isEmpty() -> showError(binding.editTextLastName, "Last name required")
                email.isEmpty() -> showError(binding.editTextEmail, "Email required")
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> showError(binding.editTextEmail, "Invalid email")
                password.isEmpty() -> showError(binding.editTextPassword, "Password required")
                password.length < 6 -> showError(binding.editTextPassword, "Password must be at least 6 characters")
                password != confirmPassword -> showError(binding.editTextConfirmPassword, "Passwords don't match")
                else -> registerUser(email, password, firstName, lastName)
            }
        }

        binding.textViewBack.setOnClickListener {
            // Use the Navigation Component to go back
            findNavController().popBackStack()
        }
    }

    private fun registerUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener

                val userMap = hashMapOf(
                    "uid" to uid,
                    "email" to email,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "role" to "user"
                )

                firestore.collection("users").document(uid)
                    .set(userMap)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Registered successfully", Toast.LENGTH_SHORT).show()
                        // Use the Navigation Component to go back
                        findNavController().popBackStack()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error saving user: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
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