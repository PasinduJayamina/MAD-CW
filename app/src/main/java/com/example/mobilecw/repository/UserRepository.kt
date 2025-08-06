package com.example.mobilecw.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

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
                            .addOnSuccessListener { doc ->
                                val role = doc.getString("role")
                                if (role != null) {
                                    onComplete(true, role, null)
                                } else {
                                    onComplete(false, null, "Role not found")
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
