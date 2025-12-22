package com.example.medhomeapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.UserModel
import com.example.medhomeapp.repository.UserRepo

import com.example.medhomeapp.utils.AuthState
import com.example.medhomeapp.utils.UiState
import com.google.firebase.auth.FirebaseAuth

class UserViewModel(private val repo: UserRepo) : ViewModel() {

    // Auth State
    private val _authState = mutableStateOf<AuthState>(AuthState.Idle)
    val authState: State<AuthState> = _authState

    // Current User
    private val _currentUser = mutableStateOf<UserModel?>(null)
    val currentUser: State<UserModel?> = _currentUser

    // All Users
    private val _allUsers = mutableStateOf<UiState<List<UserModel>>>(UiState.Idle)
    val allUsers: State<UiState<List<UserModel>>> = _allUsers

    // Loading for operations
    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    // Check if user is logged in
    fun checkAuthStatus(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    // LOGIN - FIXED: Only proceed after both Auth + DB read succeed
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading

        // Step 1: Auth login
        repo.login(email, password) { success, message ->
            if (success) {
                val userId = getCurrentUserId()
                if (userId != null) {
                    // Step 2: Check if user exists in DB
                    repo.getUserByID(userId) { dbSuccess, dbMessage, user ->
                        if (dbSuccess && user != null) {
                            _currentUser.value = user
                            _authState.value = AuthState.Success("Login successful", userId)
                        } else {
                            // User exists in Auth but not in DB - critical error
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

    // REGISTER - FIXED: Only succeed after BOTH Auth + DB write complete
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

        // Step 1: Create auth user
        repo.register(email, password) { authSuccess, authMessage, userId ->
            if (authSuccess && userId.isNotEmpty()) {
                val updatedModel = userModel.copy(
                    id = userId,
                    email = email,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = System.currentTimeMillis().toString()
                )

                // Step 2: Add to database - CRITICAL STEP
                repo.addUserToDatabase(userId, updatedModel) { dbSuccess, dbMessage ->
                    if (dbSuccess) {
                        _currentUser.value = updatedModel
                        _authState.value = AuthState.Success("Registration successful", userId)
                    } else {
                        // DB write failed - delete auth user to prevent ghost accounts
                        FirebaseAuth.getInstance().currentUser?.delete()
                        _authState.value = AuthState.Error("Failed to create profile: $dbMessage")
                    }
                }
            } else {
                _authState.value = AuthState.Error(authMessage)
            }
        }
    }

    // FORGET PASSWORD
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
}