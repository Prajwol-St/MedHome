package com.example.medhomeapp.repository

import com.example.medhomeapp.model.InventoryModel

interface InventoryRepo {
    // Create operations
    fun createInventory(
        inventory: InventoryModel,
        callback: (Boolean, String) -> Unit
    )

    fun uploadInventoryImage(
        imageUri: String,
        callback: (Boolean, String, String?) -> Unit // success, message, imageUrl
    )

    // Read operations
    fun getAllInventory(
        callback: (Boolean, String, List<InventoryModel>?) -> Unit
    )

    fun getInventoryByCurrentUser(
        callback: (Boolean, String, List<InventoryModel>?) -> Unit
    )

    fun getInventoryById(
        inventoryID: String,
        callback: (Boolean, String, InventoryModel?) -> Unit
    )

    // Update operations
    fun updateInventory(
        inventoryId: String,
        updatedInventory: InventoryModel,
        callback: (Boolean, String) -> Unit
    )

    // Delete operations
    fun deleteInventory(
        inventoryId: String,
        callback: (Boolean, String) -> Unit
    )

    // Search operations
    fun searchInventory(
        query: String,
        callback: (Boolean, String, List<InventoryModel>?) -> Unit
    )

    // Real-time listeners
    fun listenToAllInventory(
        callback: (List<InventoryModel>) -> Unit
    )

    fun listenToUserInventory(
        userID: String,
        callback: (List<InventoryModel>) -> Unit
    )

    fun stopListening()
}
