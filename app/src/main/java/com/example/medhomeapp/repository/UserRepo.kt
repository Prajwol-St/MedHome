package com.example.medhomeapp.repository

import com.example.medhomeapp.model.UserModel
import javax.security.auth.callback.Callback

interface UserRepo {

    fun login (
        email: String,
        password: String,
        callback: (Boolean, String, UserModel?) -> Unit
    )
    fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String?) -> Unit // userId
    )

    // ADD USER DETAILS TO DATABASE
    fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    // FORGOT PASSWORD
    fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    )

    // GET USER BY ID
    fun getUserById(
        userId: String,
        callback: (Boolean, String, UserModel?) -> Unit
    )

    // GET ALL USERS
    fun getAllUsers(
        callback: (Boolean, String, List<UserModel>) -> Unit
    )

    // EDIT USER PROFILE
    fun editProfile(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    // DELETE USER ACCOUNT
    fun deleteAccount(
        userId: String,
        callback: (Boolean, String) -> Unit
    )


}