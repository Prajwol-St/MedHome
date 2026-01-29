package com.example.medhomeapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medhomeapp.model.UserModel
import com.example.medhomeapp.repository.UserRepo
import com.example.medhomeapp.utils.AuthState
import com.example.medhomeapp.utils.UiState
import com.example.medhomeapp.viewmodel.UserViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class UserViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun login_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        val mockUser = UserModel(
            id = "user123",
            email = "test@example.com",
            name = "John Doe",
            role = "patient"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Login successful")
            null
        }.`when`(repo).login(any(), any(), any())

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, UserModel?) -> Unit>(1)
            callback(true, "", mockUser)
            null
        }.`when`(repo).getUserByID(any(), any())

        viewModel.login("test@example.com", "password123")

        assertEquals(mockUser, viewModel.currentUser.value)

        verify(repo).login(eq("test@example.com"), eq("password123"), any())
    }

    @Test
    fun login_error_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Invalid credentials")
            null
        }.`when`(repo).login(any(), any(), any())

        viewModel.login("test@example.com", "wrongpassword")

        assertEquals(AuthState.Error("Invalid credentials"), viewModel.authState.value)

        verify(repo).login(eq("test@example.com"), eq("wrongpassword"), any())
    }

    @Test
    fun register_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        val newUser = UserModel(
            name = "John Doe",
            email = "",
            role = "patient"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, String) -> Unit>(2)
            callback(true, "Registration successful", "user123")
            null
        }.`when`(repo).register(any(), any(), any())

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "User added to database")
            null
        }.`when`(repo).addUserToDatabase(any(), any(), any())

        viewModel.register("test@example.com", "password123", newUser)

        assertEquals("user123", viewModel.currentUser.value?.id)
        assertEquals("test@example.com", viewModel.currentUser.value?.email)

        verify(repo).register(eq("test@example.com"), eq("password123"), any())
        verify(repo).addUserToDatabase(eq("user123"), any(), any())
    }

    @Test
    fun register_error_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        val newUser = UserModel(
            name = "John Doe",
            email = "",
            role = "patient"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, String) -> Unit>(2)
            callback(false, "Email already exists", "")
            null
        }.`when`(repo).register(any(), any(), any())

        viewModel.register("test@example.com", "password123", newUser)

        assertEquals(AuthState.Error("Email already exists"), viewModel.authState.value)
        assertNull(viewModel.currentUser.value)

        verify(repo).register(eq("test@example.com"), eq("password123"), any())
    }

    @Test
    fun forgetPassword_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Password reset email sent")
            null
        }.`when`(repo).forgetPassword(any(), any())

        viewModel.forgetPassword("test@example.com")

        assertEquals(AuthState.Success("Password reset email sent"), viewModel.authState.value)

        verify(repo).forgetPassword(eq("test@example.com"), any())
    }

    @Test
    fun forgetPassword_error_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(false, "Email not found")
            null
        }.`when`(repo).forgetPassword(any(), any())

        viewModel.forgetPassword("test@example.com")

        assertEquals(AuthState.Error("Email not found"), viewModel.authState.value)

        verify(repo).forgetPassword(eq("test@example.com"), any())
    }

    @Test
    fun getUserByID_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        val mockUser = UserModel(
            id = "user123",
            email = "test@example.com",
            name = "John Doe",
            role = "patient"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, UserModel?) -> Unit>(1)
            callback(true, "", mockUser)
            null
        }.`when`(repo).getUserByID(any(), any())

        viewModel.getUserByID("user123")

        assertEquals(mockUser, viewModel.currentUser.value)

        verify(repo).getUserByID(eq("user123"), any())
    }

    @Test
    fun getUserByID_error_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, UserModel?) -> Unit>(1)
            callback(false, "User not found", null)
            null
        }.`when`(repo).getUserByID(any(), any())

        viewModel.getUserByID("user123")

        assertNull(viewModel.currentUser.value)

        verify(repo).getUserByID(eq("user123"), any())
    }

    @Test
    fun getAllUser_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        val mockUsers = listOf(
            UserModel(
                id = "1",
                email = "user1@example.com",
                name = "User One",
                role = "patient"
            ),
            UserModel(
                id = "2",
                email = "user2@example.com",
                name = "User Two",
                role = "doctor"
            )
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<UserModel>) -> Unit>(0)
            callback(true, "", mockUsers)
            null
        }.`when`(repo).getAllUser(any())

        viewModel.getAllUser()

        assertEquals(UiState.Success(mockUsers), viewModel.allUsers.value)

        verify(repo).getAllUser(any())
    }

    @Test
    fun getAllUser_error_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<UserModel>) -> Unit>(0)
            callback(false, "Failed to load users", emptyList())
            null
        }.`when`(repo).getAllUser(any())

        viewModel.getAllUser()

        assertEquals(UiState.Error("Failed to load users"), viewModel.allUsers.value)

        verify(repo).getAllUser(any())
    }

    @Test
    fun editProfile_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        val updatedUser = UserModel(
            id = "user123",
            email = "test@example.com",
            name = "John Doe Updated",
            role = "patient"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Profile updated successfully")
            null
        }.`when`(repo).editProfile(any(), any(), any())

        var callbackSuccess = false
        var callbackMessage = ""
        viewModel.editProfile("user123", updatedUser) { success, message ->
            callbackSuccess = success
            callbackMessage = message
        }

        assertEquals(true, callbackSuccess)
        assertEquals("Profile updated successfully", callbackMessage)
        assertEquals("John Doe Updated", viewModel.currentUser.value?.name)

        verify(repo).editProfile(eq("user123"), any(), any())
    }

    @Test
    fun editProfile_error_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        val updatedUser = UserModel(
            id = "user123",
            email = "test@example.com",
            name = "John Doe Updated",
            role = "patient"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Failed to update profile")
            null
        }.`when`(repo).editProfile(any(), any(), any())

        var callbackSuccess = true
        var callbackMessage = ""
        viewModel.editProfile("user123", updatedUser) { success, message ->
            callbackSuccess = success
            callbackMessage = message
        }

        assertEquals(false, callbackSuccess)
        assertEquals("Failed to update profile", callbackMessage)

        verify(repo).editProfile(eq("user123"), any(), any())
    }

    @Test
    fun deleteAccount_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Account deleted successfully")
            null
        }.`when`(repo).deleteAccount(any(), any())

        var callbackSuccess = false
        var callbackMessage = ""
        viewModel.deleteAccount("user123") { success, message ->
            callbackSuccess = success
            callbackMessage = message
        }

        assertEquals(true, callbackSuccess)
        assertEquals("Account deleted successfully", callbackMessage)

        verify(repo).deleteAccount(eq("user123"), any())
    }

    @Test
    fun deleteAccount_error_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(false, "Failed to delete account")
            null
        }.`when`(repo).deleteAccount(any(), any())

        var callbackSuccess = true
        var callbackMessage = ""
        viewModel.deleteAccount("user123") { success, message ->
            callbackSuccess = success
            callbackMessage = message
        }

        assertEquals(false, callbackSuccess)
        assertEquals("Failed to delete account", callbackMessage)

        verify(repo).deleteAccount(eq("user123"), any())
    }

    @Test
    fun changePassword_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Password changed successfully")
            null
        }.`when`(repo).changePassword(any(), any(), any())

        var callbackSuccess = false
        var callbackMessage = ""
        viewModel.changePassword("oldPassword", "newPassword123") { success, message ->
            callbackSuccess = success
            callbackMessage = message
        }

        assertEquals(true, callbackSuccess)
        assertEquals("Password changed successfully", callbackMessage)
        assertEquals(false, viewModel.loading.value)

        verify(repo).changePassword(eq("oldPassword"), eq("newPassword123"), any())
    }

    @Test
    fun changePassword_error_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Current password is incorrect")
            null
        }.`when`(repo).changePassword(any(), any(), any())

        var callbackSuccess = true
        var callbackMessage = ""
        viewModel.changePassword("wrongPassword", "newPassword123") { success, message ->
            callbackSuccess = success
            callbackMessage = message
        }

        assertEquals(false, callbackSuccess)
        assertEquals("Current password is incorrect", callbackMessage)
        assertEquals(false, viewModel.loading.value)

        verify(repo).changePassword(eq("wrongPassword"), eq("newPassword123"), any())
    }

    @Test
    fun resetAuthState_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Error")
            null
        }.`when`(repo).login(any(), any(), any())

        viewModel.login("test@example.com", "password")
        assertEquals(AuthState.Error("Error"), viewModel.authState.value)

        viewModel.resetAuthState()
        assertEquals(AuthState.Idle, viewModel.authState.value)
    }
}