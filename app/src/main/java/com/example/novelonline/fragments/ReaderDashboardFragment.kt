package com.example.novelonline.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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

    // Launcher for picking an image from the gallery
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    // Variable to hold the URI for camera-captured images
    private var imageUri: Uri? = null

    // Launcher for taking a picture with the camera
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            imageUri?.let { uri ->
                uploadProfilePicture(uri)
            }
        } else {
            Toast.makeText(requireContext(), "Image capture cancelled", Toast.LENGTH_SHORT).show()
        }
    }

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
        setupImagePickerLauncher() // This now only handles the gallery intent
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
            showImageSourceDialog() // Now calls the dialog function
        }

        binding.offlineReadingCard.setOnClickListener {
            findNavController().navigate(R.id.action_readerDashboardFragment_to_offlineBooksFragment)
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

    // This launcher setup is now specifically for the gallery
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

    private fun showImageSourceDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Upload Profile Picture")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> takePicture()
                    1 -> pickImageFromGallery()
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun takePicture() {
        imageUri = createImageUri()
        imageUri?.let { takePictureLauncher.launch(it) }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        pickImageLauncher.launch(intent)
    }

    private fun createImageUri(): Uri? {
        val contentResolver = requireContext().contentResolver
        val fileName = "profile_picture_${System.currentTimeMillis()}.jpg"
        val contentValues = android.content.ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
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