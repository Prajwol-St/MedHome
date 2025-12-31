package com.example.medhomeapp.model

data class InventoryModel(
    var inventoryID: String = "",
    var userID: String = "",
    var medicineName: String = "",
    var description: String = "",
    var price: String = "",
    var amount: String = "",
    var imageUrl: String = "",
    var timestamp: Long = 0L
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
}

