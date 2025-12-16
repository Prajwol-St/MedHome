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


    fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    )


    fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    )


    fun getUserById(
        userId: String,
        callback: (Boolean, String, UserModel?) -> Unit
    )


    fun getAllUsers(
        callback: (Boolean, String, List<UserModel>) -> Unit
    )


    fun editProfile(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    )


    fun deleteAccount(
        userId: String,
        callback: (Boolean, String) -> Unit
    )

    fun logout()

    fun deleteAuthUser(
        callback: (Boolean, String) -> Unit)


}