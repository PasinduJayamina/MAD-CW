package com.example.mobilecw.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.mobilecw.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        val firstNameEditText = view.findViewById<EditText>(R.id.editTextFirstName)
        val lastNameEditText = view.findViewById<EditText>(R.id.editTextLastName)
        val emailEditText = view.findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = view.findViewById<EditText>(R.id.editTextPassword)
        val confirmPasswordEditText = view.findViewById<EditText>(R.id.editTextConfirmPassword)
        val spinnerRole = view.findViewById<Spinner>(R.id.spinnerRole)
        val signUpButton = view.findViewById<Button>(R.id.buttonSignUp)
        val backText = view.findViewById<TextView>(R.id.textViewBack)


        val roles = arrayOf("Select Role", "reader", "writer")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = adapter


        signUpButton.setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            val role = spinnerRole.selectedItem.toString()

            when {
                firstName.isEmpty() -> showError(firstNameEditText, "First name required")
                lastName.isEmpty() -> showError(lastNameEditText, "Last name required")
                email.isEmpty() -> showError(emailEditText, "Email required")
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> showError(emailEditText, "Invalid email")
                password.isEmpty() -> showError(passwordEditText, "Password required")
                password.length < 6 -> showError(passwordEditText, "Password must be at least 6 characters")
                password != confirmPassword -> showError(confirmPasswordEditText, "Passwords don't match")
                role == "Select Role" -> Toast.makeText(requireContext(), "Please select a role", Toast.LENGTH_SHORT).show()
                else -> registerUser(email, password, firstName, lastName, role)
            }
        }


        backText.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun registerUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        role: String
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener

                val userMap = hashMapOf(
                    "uid" to uid,
                    "email" to email,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "role" to role
                )

                firestore.collection("users").document(uid)
                    .set(userMap)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Registered successfully", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
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
}