package com.example.medhomeapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.medhomeapp.model.NotificationHistoryModel
import com.example.medhomeapp.repository.NotificationRepository
import com.example.medhomeapp.viewmodel.NotificationHistoryViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class NotificationHistoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadNotifications_success_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = NotificationHistoryViewModel(repo)

        val mockNotifications = listOf(
            NotificationHistoryModel(
                notificationId = "1",
                userId = "user123",
                title = "Appointment Reminder",
                message = "Your appointment is tomorrow",
                type = "appointment",
                isRead = false
            ),
            NotificationHistoryModel(
                notificationId = "2",
                userId = "user123",
                title = "Medicine Reminder",
                message = "Take your medicine",
                type = "medicine",
                isRead = true
            )
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<(List<NotificationHistoryModel>) -> Unit>(1)
            callback(mockNotifications)
            null
        }.`when`(repo).getNotificationHistory(any(), any())

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Int) -> Unit>(1)
            callback(1)
            null
        }.`when`(repo).getUnreadNotificationCount(any(), any())

        viewModel.loadNotifications("user123")

        assertEquals(mockNotifications, viewModel.notifications.value)
        assertEquals(1, viewModel.unreadCount.value)
        assertEquals(false, viewModel.isLoading.value)

        verify(repo).getNotificationHistory(eq("user123"), any())
        verify(repo).getUnreadNotificationCount(eq("user123"), any())
    }

    @Test
    fun markAsRead_success_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = NotificationHistoryViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Marked as read")
            null
        }.`when`(repo).markNotificationAsRead(any(), any(), any())

        viewModel.markAsRead("user123", "notification123")

        assertNull(viewModel.errorMessage.value)

        verify(repo).markNotificationAsRead(eq("user123"), eq("notification123"), any())
    }

    @Test
    fun markAsRead_error_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = NotificationHistoryViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Failed to mark as read")
            null
        }.`when`(repo).markNotificationAsRead(any(), any(), any())

        viewModel.markAsRead("user123", "notification123")

        assertEquals("Failed to mark as read", viewModel.errorMessage.value)

        verify(repo).markNotificationAsRead(eq("user123"), eq("notification123"), any())
    }

    @Test
    fun markAllAsRead_success_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = NotificationHistoryViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "All marked as read")
            null
        }.`when`(repo).markAllNotificationsAsRead(any(), any())

        viewModel.markAllAsRead("user123")

        assertNull(viewModel.errorMessage.value)
        assertEquals(0, viewModel.unreadCount.value)

        verify(repo).markAllNotificationsAsRead(eq("user123"), any())
    }

    @Test
    fun markAllAsRead_error_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = NotificationHistoryViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(false, "Failed to mark all as read")
            null
        }.`when`(repo).markAllNotificationsAsRead(any(), any())

        viewModel.markAllAsRead("user123")

        assertEquals("Failed to mark all as read", viewModel.errorMessage.value)

        verify(repo).markAllNotificationsAsRead(eq("user123"), any())
    }

    @Test
    fun deleteNotification_success_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = NotificationHistoryViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Deleted successfully")
            null
        }.`when`(repo).deleteNotificationFromHistory(any(), any(), any())

        viewModel.deleteNotification("user123", "notification123")

        assertNull(viewModel.errorMessage.value)

        verify(repo).deleteNotificationFromHistory(eq("user123"), eq("notification123"), any())
    }

    @Test
    fun deleteNotification_error_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = NotificationHistoryViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Failed to delete")
            null
        }.`when`(repo).deleteNotificationFromHistory(any(), any(), any())

        viewModel.deleteNotification("user123", "notification123")

        assertEquals("Failed to delete", viewModel.errorMessage.value)

        verify(repo).deleteNotificationFromHistory(eq("user123"), eq("notification123"), any())
    }

    @Test
    fun clearError_test() {
        val repo = mock<NotificationRepository>()
        val viewModel = NotificationHistoryViewModel(repo)

        viewModel.errorMessage.value = "Error message"

        viewModel.clearError()

        assertNull(viewModel.errorMessage.value)
    }
}