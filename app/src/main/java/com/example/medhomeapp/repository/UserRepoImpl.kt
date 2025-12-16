package com.example.medhomeapp.repository

import com.example.medhomeapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserRepoImpl : UserRepo {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("User")

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        getUserById(userId) { success, _, user ->
                            callback(true, "Login success", if (success) user else null)
                        }
                    } else {
                        callback(true, "Login success", null)
                    }
                } else {
                    callback(false, task.exception?.message ?: "Login failed", null)
                }
            }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    callback(true, "Registration success", uid)
                } else {
                    callback(false, task.exception?.message ?: "Registration failed", "")
                }
            }
    }

    override fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).setValue(model)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "User data saved")
                } else {
                    callback(false, task.exception?.message ?: "Failed to save user")
                }
            }
    }

    override fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Reset link sent to $email")
                } else {
                    callback(false, task.exception?.message ?: "Failed to send reset link")
                }
            }
    }

    override fun getUserById(
        userId: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ) {
        ref.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    callback(false, "User not found", null)
                    return
                }
                val user = snapshot.getValue(UserModel::class.java)
                callback(true, "User fetched", user)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun getAllUsers(callback: (Boolean, String, List<UserModel>) -> Unit) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    callback(true, "No users found", emptyList())
                    return
                }
                val users = snapshot.children.mapNotNull { it.getValue(UserModel::class.java) }
                callback(true, "Users fetched", users)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun editProfile(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        ref.child(userId).updateChildren(model.toMap())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) callback(true, "Profile updated")
                else callback(false, task.exception?.message ?: "Failed to update profile")
            }
    }

    override fun deleteAccount(userId: String, callback: (Boolean, String) -> Unit) {
        ref.child(userId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) callback(true, "Account deleted")
                else callback(false, task.exception?.message ?: "Failed to delete account")
            }
    }

    override fun logout() {
        auth.signOut()
    }

    override fun deleteAuthUser(callback: (Boolean, String) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            callback(false, "No authenticated user")
            return
        }
        user.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) callback(true, "Auth account deleted")
            else callback(false, task.exception?.message ?: "Failed to delete auth user")
        }
    }
}
