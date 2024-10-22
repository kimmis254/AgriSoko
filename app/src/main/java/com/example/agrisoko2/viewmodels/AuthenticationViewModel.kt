package com.example.agrisoko2.viewmodels

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class AuthenticationViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun signInWithGoogle(
        activity: Activity,
        googleSignInClient: GoogleSignInClient,
        onGoogleSignInResult: (Intent) -> Unit
    ){
        val signInIntent = googleSignInClient.signInIntent
        onGoogleSignInResult(signInIntent)
    }

    fun handleGoogleSignInResult(account: GoogleSignInAccount?, onResult: (Boolean, String?, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val userDoc = firestore.collection("users").document(userId ?: return@addOnCompleteListener)

                    userDoc.get().addOnSuccessListener { document ->
                        val role = document.getString("role")
                        if (role != null) {
                            onResult(true, role, null)  // Existing user with a role
                        } else {
                            onResult(true, null, null)  // New user, no role assigned yet
                        }
                    }.addOnFailureListener { e ->
                        onResult(false, null, e.message)
                    }
                } else {
                    onResult(false, null, task.exception?.message)
                }
            }
    }


    fun loginUser(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            firestore.collection("users").document(userId)
                                .get()
                                .addOnSuccessListener { document ->
                                    val role = document.getString("role")
                                    role?.let {
                                        onSuccess(it)  // Success: send role ("farmer" or "customer")
                                    }
                                }
                                .addOnFailureListener {
                                    onFailure("Failed to retrieve user role.")
                                }
                        }
                    } else {
                        onFailure("Authentication failed.")
                    }
                }
        }
    }

    fun registerUser(
        email: String,
        password: String,
        name: String,
        contact: String,
        role: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            val userMap = mapOf(
                                "email" to email,
                                "role" to role,
                                "name" to name,
                                "phoneNumber" to contact
                            )
                            firestore.collection("users").document(userId)
                                .set(userMap)
                                .addOnSuccessListener {
                                    onSuccess()  // User successfully registered
                                }
                                .addOnFailureListener {
                                    onFailure("Failed to save user details. Please try again")
                                }
                        }
                    } else {
                        onFailure(task.exception?.message?:"Registration failed.")
                    }
                }
        }
    }
    fun saveUserRole(userId: String, role: String, onResult: (Boolean, String?) -> Unit) {
        val userDoc = firestore.collection("users").document(userId)
        userDoc.update("role", role)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }
    fun uploadProfilePicture(uri: Uri, userId: String, onUploadSuccess: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("users/$userId/profilePicture")
        storageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val profileImageUrl = uri.toString()
                    // Update user's profile in Firestore
                    firestore.collection("users").document(userId)
                        .update("profilePicture", profileImageUrl)
                    onUploadSuccess(profileImageUrl)
                }
            }
    }


}
