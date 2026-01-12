package com.example.medhomeapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.UserModel
import com.example.medhomeapp.repository.UserRepo
import com.example.medhomeapp.utils.AuthState
import com.example.medhomeapp.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import android.net.Uri
import com.example.medhomeapp.repository.CommonRepo

class UserViewModel(private val repo: UserRepo) : ViewModel() {

    private val _authState = mutableStateOf<AuthState>(AuthState.Idle)
    val authState: State<AuthState> = _authState

    private val _currentUser = mutableStateOf<UserModel?>(null)
    val currentUser: State<UserModel?> = _currentUser

    private val _allUsers = mutableStateOf<UiState<List<UserModel>>>(UiState.Idle)
    val allUsers: State<UiState<List<UserModel>>> = _allUsers

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    fun checkAuthStatus(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }


    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading

        repo.login(email, password) { success, message ->
            if (success) {
                val userId = getCurrentUserId()
                if (userId != null) {
                    repo.getUserByID(userId) { dbSuccess, dbMessage, user ->
                        if (dbSuccess && user != null) {
                            _currentUser.value = user
                            _authState.value = AuthState.Success("Login successful", userId)
                        } else {
                            _authState.value = AuthState.Error("User profile not found. Please contact support.")
                        }
                    }
                } else {
                    _authState.value = AuthState.Error("Failed to get user ID")
                }
            } else {
                _authState.value = AuthState.Error(message)
            }
        }
    }

    fun register(email: String, password: String, userModel: UserModel) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }

        _authState.value = AuthState.Loading

        repo.register(email, password) { authSuccess, authMessage, userId ->
            if (authSuccess && userId.isNotEmpty()) {
                val updatedModel = userModel.copy(
                    id = userId,
                    email = email,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = System.currentTimeMillis().toString()
                )

                repo.addUserToDatabase(userId, updatedModel) { dbSuccess, dbMessage ->
                    if (dbSuccess) {
                        _currentUser.value = updatedModel
                        _authState.value = AuthState.Success("Registration successful", userId)
                    } else {
                        FirebaseAuth.getInstance().currentUser?.delete()
                        _authState.value = AuthState.Error("Failed to create profile: $dbMessage")
                    }
                }
            } else {
                _authState.value = AuthState.Error(authMessage)
            }
        }
    }

    fun forgetPassword(email: String) {
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Email cannot be empty")
            return
        }

        _authState.value = AuthState.Loading

        repo.forgetPassword(email) { success, message ->
            if (success) {
                _authState.value = AuthState.Success(message)
            } else {
                _authState.value = AuthState.Error(message)
            }
        }
    }


    fun getUserByID(userId: String) {
        repo.getUserByID(userId) { success, message, data ->
            if (success && data != null) {
                _currentUser.value = data
            } else {
                _currentUser.value = null
            }
        }
    }


    fun getAllUser() {
        _allUsers.value = UiState.Loading

        repo.getAllUser { success, message, data ->
            if (success) {
                _allUsers.value = UiState.Success(data)
            } else {
                _allUsers.value = UiState.Error(message)
            }
        }
    }


    fun editProfile(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        val updatedModel = model.copy(
            updatedAt = System.currentTimeMillis().toString()
        )

        repo.editProfile(userId, updatedModel) { success, message ->
            if (success) {
                _currentUser.value = updatedModel
            }
            callback(success, message)
        }
    }


    fun deleteAccount(userId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteAccount(userId, callback)
    }


    fun logout() {
        FirebaseAuth.getInstance().signOut()
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }


    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
    fun uploadProfilePicture(
        context: Context,
        userId: String,
        imageUri: Uri,
        commonRepo: CommonRepo,
        callback: (Boolean, String) -> Unit
    ) {
        _loading.value = true

        commonRepo.uploadImage(context, imageUri, "profile_pictures") { success, message, imageUrl, publicId ->
            if (success && imageUrl != null) {
                val currentUserData = _currentUser.value
                if (currentUserData != null) {
                    val updatedUser = currentUserData.copy(
                        profileImageUrl = imageUrl,
                        profileImagePublicId = publicId ?: "",
                        updatedAt = System.currentTimeMillis().toString()
                    )

                    repo.editProfile(userId, updatedUser) { editSuccess, editMessage ->
                        _loading.value = false
                        if (editSuccess) {
                            _currentUser.value = updatedUser
                            callback(true, "Profile picture updated successfully!")
                        } else {
                            callback(false, editMessage)
                        }
                    }
                } else {
                    _loading.value = false
                    callback(false, "User data not found")
                }
            } else {
                _loading.value = false
                callback(false, message)
            }
        }
    }

}