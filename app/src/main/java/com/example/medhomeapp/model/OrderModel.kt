package com.example.medhomeapp.model

data class OrderModel(
    var orderID: String = "",
    var userID: String = "",
    var inventoryID: String = "",
    var medicineName: String = "",
    var price: String = "",
    var quantity: Int = 0,
    var totalAmount: Double = 0.0,
    var imageUrl: String = "",
    var orderStatus: String = "Pending", // Pending, Processing, Completed, Cancelled
    var deliveryAddress: String = "",
    var phoneNumber: String = "",
    var timestamp: Long = 0L,
    var updatedAt: Long = 0L
) {
    fun getFormattedTime(): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60000 -> "Just now"
            diff < 3600000 -> "${diff / 60000}m ago"
            diff < 86400000 -> "${diff / 3600000}h ago"
            diff < 604800000 -> "${diff / 86400000}d ago"
            else -> {
                val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                sdf.format(java.util.Date(timestamp))
            }
        }
    }

    fun getFormattedStatus(): String {
        return when (orderStatus) {
            "Pending" -> "⏳ Pending"
            "Processing" -> "📦 Processing"
            "Completed" -> "✅ Completed"
            "Cancelled" -> "❌ Cancelled"
            else -> orderStatus
        }
    }
}
