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
        callback: (Boolean, String, String?) -> Unit
    ) {
        userRepo.register(email, password) { success, message, uid ->
            Log.d("AuthViewModel", "Register called - success: $success, message: $message, uid: $uid")
            callback(success, message, uid)
        }
    }

    fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        userRepo.addUserToDatabase(userId, model, callback)
    }

    fun forgotPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        userRepo.forgetPassword(email, callback)
    }
}
