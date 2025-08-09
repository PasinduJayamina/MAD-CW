package com.example.novelonline.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.novelonline.R
import com.example.novelonline.databinding.FragmentReaderDashboardBinding
import com.example.novelonline.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class ReaderDashboardFragment : Fragment() {

    private var _binding: FragmentReaderDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReaderDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        setupClickListeners()
        setupImagePickerLauncher()
        loadUserProfile()
    }

    private fun setupClickListeners() {
        binding.becomeWriterCard.setOnClickListener {
            findNavController().navigate(R.id.action_readerDashboardFragment_to_becomeWriterFragment)
        }

        binding.yourWorksCard.setOnClickListener {
            findNavController().navigate(R.id.action_readerDashboardFragment_to_yourWorksFragment)
        }

        binding.bookFairsCard.setOnClickListener {
            findNavController().navigate(R.id.action_readerDashboardFragment_to_bookFairsFragment)
        }

        binding.editProfileIcon.setOnClickListener {
            openImagePicker()
        }
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userRef = firestore.collection("users").document(currentUser.uid)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        user?.let {
                            // Combine first name and last name to create the full name
                            val fullName = "${it.firstName} ${it.lastName}".trim()
                            binding.userNameTextView.text = fullName
                            if (it.profilePictureUrl.isNotEmpty()) {
                                Glide.with(this)
                                    .load(it.profilePictureUrl)
                                    .placeholder(R.drawable.ic_default_profile)
                                    .error(R.drawable.ic_default_profile)
                                    .into(binding.profilePictureImageView)
                            }
                        }
                    } else {
                        Toast.makeText(context, "User data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to load user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupImagePickerLauncher() {
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    uploadProfilePicture(imageUri)
                }
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        pickImageLauncher.launch(intent)
    }

    private fun uploadProfilePicture(imageUri: Uri) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(context, "User not authenticated.", Toast.LENGTH_SHORT).show()
            return
        }

        val profileRef = storage.reference.child("users/${currentUser.uid}/profile_picture.jpg")

        Glide.with(this).load(imageUri).into(binding.profilePictureImageView)
        Toast.makeText(context, "Uploading profile picture...", Toast.LENGTH_SHORT).show()

        profileRef.putFile(imageUri)
            .addOnSuccessListener {
                profileRef.downloadUrl.addOnSuccessListener { uri ->
                    val profilePictureUrl = uri.toString()
                    updateUserDocument(profilePictureUrl)
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to get download URL.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserDocument(profilePictureUrl: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userRef = firestore.collection("users").document(currentUser.uid)
            userRef.update("profilePictureUrl", profilePictureUrl)
                .addOnSuccessListener {
                    Toast.makeText(context, "Profile picture updated successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}