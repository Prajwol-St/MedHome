package com.example.medhomeapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.UserModel
import com.example.medhomeapp.repository.UserRepoImpl

class AuthViewModel: ViewModel() {
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
        age: Int,
        bloodGroup: String,
        emergencyContact: String,
        address: String,
        callback: (Boolean, String) -> Unit
    ) {
        userRepo.register(email, password) { success, message, uid ->
            Log.d("AuthViewModel", "Register called - success: $success, message: $message, uid: $uid")

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
                    age = age,
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

    fun forgotPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        userRepo.forgetPassword(email, callback)
    }

    fun checkIfUserExists(
        userId: String,
        callback: (Boolean, UserModel?) -> Unit
    ) {
        userRepo.getUserById(userId) { success, message, user ->
            callback(success, user)
        }
    }
}