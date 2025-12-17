package com.example.medhomeapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.UserModel
import com.example.medhomeapp.repository.UserRepoImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthViewModel : ViewModel() {

    private val userRepo = UserRepoImpl()

    fun login(
        email: String,
        password: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ) {
        userRepo.login(email, password, callback)
    }

    fun register(
        email: String,
        password: String,
        name: String,
        contact: String,
        gender: String,
        dateOfBirth: String,
        bloodGroup: String,
        emergencyContact: String,
        address: String,
        callback: (Boolean, String) -> Unit
    ) {
        userRepo.register(email, password) { success, message, uid ->
            if (success && uid != null) {
                val timestamp = System.currentTimeMillis().toString()

                val userModel = UserModel(
                    id = uid,
                    role = "patient",
                    name = name,
                    email = email,
                    passwordHash = password,
                    contact = contact,
                    gender = gender,
                    dateOfBirth = dateOfBirth,
                    emailVerified = false,
                    createdAt = timestamp,
                    updatedAt = timestamp,
                    bloodGroup = bloodGroup,
                    emergencyContact = emergencyContact,
                    address = address
                )

                userRepo.addUserToDatabase(uid, userModel) { dbSuccess, dbMessage ->
                    callback(dbSuccess, dbMessage)
                }
            } else {
                callback(false, message)
            }
        }
    }

    fun forgotPassword(email: String, callback: (Boolean, String) -> Unit) {
        userRepo.forgetPassword(email, callback)
    }

    fun checkEmailExists(email: String, callback: (Boolean) -> Unit) {
        userRepo.getAllUsers { success, _, users ->
            callback(success && users.any { it.email.equals(email, true) })
        }
    }

    fun checkPhoneExists(phone: String, callback: (Boolean) -> Unit) {
        userRepo.getAllUsers { success, _, users ->
            callback(success && users.any { it.contact == phone })
        }
    }

    fun firebaseSignInWithGoogle(
        idToken: String,
        callback: (Boolean, String?) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        FirebaseAuth.getInstance()
            .signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, FirebaseAuth.getInstance().currentUser?.uid)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun checkIfUserExists(
        userId: String,
        callback: (Boolean, UserModel?) -> Unit
    ) {
        userRepo.getUserById(userId) { success, _, user ->
            callback(success && user != null, user)
        }

    }
    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
}

