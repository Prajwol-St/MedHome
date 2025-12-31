package com.example.medhomeapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.InventoryModel
import com.example.medhomeapp.repository.InventoryRepo

class InventoryViewModel(private val repo: InventoryRepo) : ViewModel() {

    // LiveData for loading state
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    // LiveData for inventory creation/update status
    private val _inventoryStatus = MutableLiveData<Pair<Boolean, String>>()
    val inventoryStatus: LiveData<Pair<Boolean, String>> get() = _inventoryStatus

    // LiveData for all inventory items
    private val _allInventory = MutableLiveData<List<InventoryModel>>()
    val allInventory: LiveData<List<InventoryModel>> get() = _allInventory

    /**
     * Create a new inventory item
     */
    fun createInventory(inventory: InventoryModel) {
        _loading.postValue(true)
        repo.createInventory(inventory) { success, message ->
            _loading.postValue(false)
            _inventoryStatus.postValue(Pair(success, message))
            if (success) {
                // Refresh inventory list after creation
                getCurrentUserInventory()
            }
            Log.d("InventoryViewModel", "Inventory creation: $success - $message")
        }
    }

    /**
     * Upload image for inventory
     */
    fun uploadInventoryImage(imageUri: String, callback: (Boolean, String, String?) -> Unit) {
        _loading.postValue(true)
        repo.uploadInventoryImage(imageUri) { success, message, imageUrl ->
            _loading.postValue(false)
            callback(success, message, imageUrl)
            Log.d("InventoryViewModel", "Image upload: $success - $message")
        }
    }

    /**
     * Create inventory with image
     */
    fun createInventoryWithImage(
        medicineName: String,
        description: String,
        price: String,
        amount: String,
        imageUri: String?
    ) {
        if (medicineName.isBlank() || price.isBlank() || amount.isBlank()) {
            _inventoryStatus.postValue(Pair(false, "Medicine name, price, and amount cannot be empty"))
            return
        }

        if (imageUri != null) {
            // First upload image, then create inventory
            uploadInventoryImage(imageUri) { success, message, uploadedImageUrl ->
                if (success && uploadedImageUrl != null) {
                    val inventory = InventoryModel(
                        medicineName = medicineName,
                        description = description,
                        price = price,
                        amount = amount,
                        imageUrl = uploadedImageUrl
                    )
                    createInventory(inventory)
                } else {
                    _inventoryStatus.postValue(Pair(false, "Failed to upload image: $message"))
                }
            }
        } else {
            // Create inventory without image
            val inventory = InventoryModel(
                medicineName = medicineName,
                description = description,
                price = price,
                amount = amount,
                imageUrl = ""
            )
            createInventory(inventory)
        }
    }

    /**
     * Get all inventory items
     */
    fun getAllInventory() {
        _loading.postValue(true)
        repo.getAllInventory { success, message, inventoryList ->
            _loading.postValue(false)
            if (success && inventoryList != null) {
                _allInventory.postValue(inventoryList)
            } else {
                _allInventory.postValue(emptyList())
            }
            Log.d("InventoryViewModel", "All inventory: $success - $message")
        }
    }

    /**
     * Get current user's inventory
     */
    fun getCurrentUserInventory() {
        _loading.postValue(true)
        repo.getInventoryByCurrentUser { success, message, inventoryList ->
            _loading.postValue(false)
            if (success && inventoryList != null) {
                _allInventory.postValue(inventoryList)
            } else {
                _allInventory.postValue(emptyList())
            }
            Log.d("InventoryViewModel", "Current user inventory: $success - $message")
        }
    }

    /**
     * Update inventory item
     */
    fun updateInventory(inventoryId: String, updatedInventory: InventoryModel) {
        _loading.postValue(true)
        repo.updateInventory(inventoryId, updatedInventory) { success, message ->
            _loading.postValue(false)
            _inventoryStatus.postValue(Pair(success, message))
            if (success) {
                // Refresh inventory after update
                getCurrentUserInventory()
            }
            Log.d("InventoryViewModel", "Inventory update: $success - $message")
        }
    }

    /**
     * Update inventory with new image
     */
    fun updateInventoryWithImage(
        inventoryId: String,
        currentInventory: InventoryModel,
        medicineName: String,
        description: String,
        price: String,
        amount: String,
        imageUri: String?
    ) {
        if (medicineName.isBlank() || price.isBlank() || amount.isBlank()) {
            _inventoryStatus.postValue(Pair(false, "Medicine name, price, and amount cannot be empty"))
            return
        }

        if (imageUri != null) {
            // Upload new image first
            uploadInventoryImage(imageUri) { success, message, uploadedImageUrl ->
                if (success && uploadedImageUrl != null) {
                    val updatedInventory = currentInventory.copy(
                        medicineName = medicineName,
                        description = description,
                        price = price,
                        amount = amount,
                        imageUrl = uploadedImageUrl
                    )
                    updateInventory(inventoryId, updatedInventory)
                } else {
                    _inventoryStatus.postValue(Pair(false, "Failed to upload image: $message"))
                }
            }
        } else {
            // Update without changing image
            val updatedInventory = currentInventory.copy(
                medicineName = medicineName,
                description = description,
                price = price,
                amount = amount
            )
            updateInventory(inventoryId, updatedInventory)
        }
    }

    /**
     * Delete inventory item
     */
    fun deleteInventory(inventoryId: String) {
        _loading.postValue(true)
        repo.deleteInventory(inventoryId) { success, message ->
            _loading.postValue(false)
            _inventoryStatus.postValue(Pair(success, message))
            if (success) {
                // Refresh inventory after deletion
                getCurrentUserInventory()
            }
            Log.d("InventoryViewModel", "Inventory deletion: $success - $message")
        }
    }

    /**
     * Search inventory
     */
    fun searchInventory(query: String) {
        _loading.postValue(true)
        repo.searchInventory(query) { success, message, inventoryList ->
            _loading.postValue(false)
            if (success && inventoryList != null) {
                _allInventory.postValue(inventoryList)
            } else {
                _allInventory.postValue(emptyList())
            }
            Log.d("InventoryViewModel", "Search inventory: $success - $message")
        }
    }

    /**
     * Listen to real-time inventory updates
     */
    fun listenToInventoryUpdates() {
        repo.listenToAllInventory { inventoryList ->
            _allInventory.postValue(inventoryList)
            Log.d("InventoryViewModel", "Real-time inventory update: ${inventoryList.size} items")
        }
    }

    /**
     * Listen to user-specific inventory updates
     */
    fun listenToUserInventoryUpdates(userID: String) {
        repo.listenToUserInventory(userID) { inventoryList ->
            _allInventory.postValue(inventoryList)
            Log.d("InventoryViewModel", "Real-time user inventory update: ${inventoryList.size} items")
        }
    }

    /**
     * Stop listening to real-time updates
     */
    fun stopListening() {
        repo.stopListening()
        Log.d("InventoryViewModel", "Stopped listening to inventory updates")
    }

    /**
     * Clear status for UI management
     */
    fun clearStatus() {
        _inventoryStatus.postValue(Pair(false, ""))
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}
