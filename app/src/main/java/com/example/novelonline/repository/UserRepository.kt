package com.example.novelonline.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

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
                        usersCollection.document(uid).get()
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

    // This function is needed to get author names for the book lists
    suspend fun getUserName(userId: String): String {
        return try {
            val document = usersCollection.document(userId).get().await()
            val firstName = document.getString("firstName") ?: ""
            val lastName = document.getString("lastName") ?: ""
            "$firstName $lastName".trim()
        } catch (e: Exception) {
            "Unknown Author"
        }
    }
}