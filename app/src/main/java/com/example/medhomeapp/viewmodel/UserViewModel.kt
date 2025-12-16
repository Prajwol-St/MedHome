package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.UserModel
import com.example.medhomeapp.repository.UserRepoImpl

class UserViewModel : ViewModel() {

    private val userRepo = UserRepoImpl()

    fun addUserToDatabase(userId: String, user: UserModel, callback: (Boolean, String) -> Unit) {
        userRepo.addUserToDatabase(userId, user, callback)
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        userRepo.forgetPassword(email, callback)
    }

    fun getUserById(userId: String, callback: (Boolean, String, UserModel?) -> Unit) {
        userRepo.getUserById(userId, callback)
    }

    fun getAllUsers(callback: (Boolean, String, List<UserModel>) -> Unit) {
        userRepo.getAllUsers(callback)
    }

    fun editUserProfile(userId: String, user: UserModel, callback: (Boolean, String) -> Unit) {
        userRepo.editProfile(userId, user, callback)
    }

    fun deleteUser(userId: String, callback: (Boolean, String) -> Unit) {
        userRepo.deleteAccount(userId, callback)
    }

    fun logout() {
        userRepo.logout()
    }

    fun deleteAuthAccount(callback: (Boolean, String) -> Unit) {
        userRepo.deleteAuthUser(callback)
    }
}
