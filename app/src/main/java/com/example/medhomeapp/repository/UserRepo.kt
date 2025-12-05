package com.example.medhomeapp.repository

import com.example.medhomeapp.model.UserModel

interface UserRepo {
    fun login(email: String, password: String,
              callback: (Boolean, String?) -> Unit)


    fun signup(user: UserModel.User, password: String,
               callback: (Boolean, String?) -> Unit)


    fun addUserToDB(user: UserModel.User,
                    callback: (Boolean, String?) -> Unit)


    fun getUserById(userId: String,
                    callback: (UserModel.User?) -> Unit)


    fun getAllUsers(callback: (List<UserModel.User>) -> Unit)



    fun editProfile(user: UserModel.User,
                    callback: (Boolean, String?) -> Unit)



    fun deleteAccount(userId: String, callback:
        (Boolean, String?) -> Unit)



    fun resetPassword(email: String,
                      callback: (Boolean, String?) -> Unit)



}