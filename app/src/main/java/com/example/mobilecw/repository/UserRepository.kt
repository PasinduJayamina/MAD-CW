package com.example.mobilecw.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()


    fun signUp(
        email: String,
        password: String,
        role: String,
        firstName: String,
        lastName: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val userMap = mapOf(
                        "email" to email,
                        "role" to role,
                        "firstName" to firstName,
                        "lastName" to lastName
                    )
                    userId?.let {
                        firestore.collection("users").document(it).set(userMap)
                            .addOnSuccessListener { onComplete(true, null) }
                            .addOnFailureListener { e -> onComplete(false, e.message) }
                    } ?: onComplete(false, "User ID not found")
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }


    fun login(
        email: String,
        password: String,
        onComplete: (success: Boolean, role: String?, errorMessage: String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        firestore.collection("users").document(uid).get()
                            .addOnSuccessListener { document ->
                                if (document != null && document.exists()) {
                                    val role = document.getString("role")
                                    onComplete(true, role, null)
                                } else {
                                    onComplete(false, null, "User profile not found")
                                }
                            }
                            .addOnFailureListener { e ->
                                onComplete(false, null, e.message)
                            }
                    } else {
                        onComplete(false, null, "User ID not found")
                    }
                } else {
                    onComplete(false, null, task.exception?.message)
                }
            }
    }
}
