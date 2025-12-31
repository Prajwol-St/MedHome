package com.example.medhomeapp.repository

import android.util.Log
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.medhomeapp.model.InventoryModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.util.UUID

class InventoryRepositoryImpl : InventoryRepo {
    private val database = FirebaseDatabase.getInstance()
    private val ref = database.reference.child("inventory")
    private val auth = FirebaseAuth.getInstance()

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dahtdixmf",
            "api_key" to "869547321896952",
            "api_secret" to "Xnq2hDhTGZXUZ-w3O1xUhjTIOoI",
        )
    )

    override fun createInventory(
        inventory: InventoryModel,
        callback: (Boolean, String) -> Unit
    ) {
        try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                callback(false, "User not authenticated")
                return
            }

            val userId = currentUser.uid
            Log.d("InventoryRepository", "Creating inventory for user: $userId")

            // Generate unique inventory ID
            val inventoryId = ref.push().key ?: UUID.randomUUID().toString()

            // Create inventory with user data
            val inventoryData = inventory.copy(
                inventoryID = inventoryId,
                userID = userId,
                timestamp = System.currentTimeMillis()
            )

            Log.d("InventoryRepository", "Creating inventory with ID: $inventoryId")

            // Save to Firebase
            ref.child(inventoryId).setValue(inventoryData)
                .addOnSuccessListener {
                    Log.d("InventoryRepository", "Inventory created successfully with ID: $inventoryId")
                    callback(true, "Medicine added successfully")
                }
                .addOnFailureListener { exception ->
                    Log.e("InventoryRepository", "Failed to create inventory", exception)
                    callback(false, "Failed to add medicine: ${exception.message}")
                }

        } catch (e: Exception) {
            Log.e("InventoryRepository", "Error creating inventory", e)
            callback(false, "Error adding medicine: ${e.message}")
        }
    }

    override fun uploadInventoryImage(
        imageUri: String,
        callback: (Boolean, String, String?) -> Unit
    ) {
        try {
            // Run upload in background thread
            Thread {
                try {
                    val file = File(imageUri)
                    if (!file.exists()) {
                        callback(false, "Image file not found", null)
                        return@Thread
                    }

                    // Upload to Cloudinary with inventory-specific folder
                    val uploadResult = cloudinary.uploader().upload(
                        file,
                        ObjectUtils.asMap(
                            "folder", "inventory",
                            "resource_type", "image",
                            "quality", "auto:good",
                            "fetch_format", "auto"
                        )
                    )

                    val imageUrl = uploadResult["secure_url"] as? String
                    if (imageUrl != null) {
                        Log.d("InventoryRepository", "Image uploaded successfully: $imageUrl")
                        callback(true, "Image uploaded successfully", imageUrl)
                    } else {
                        Log.e("InventoryRepository", "Failed to get image URL from upload result")
                        callback(false, "Failed to get image URL", null)
                    }

                } catch (e: Exception) {
                    Log.e("InventoryRepository", "Error uploading image", e)
                    callback(false, "Error uploading image: ${e.message}", null)
                }
            }.start()

        } catch (e: Exception) {
            Log.e("InventoryRepository", "Error starting image upload", e)
            callback(false, "Error starting image upload: ${e.message}", null)
        }
    }

    // Helper method for complete inventory creation with image
    fun createInventoryWithImage(
        medicineName: String,
        description: String,
        price: String,
        amount: String,
        imageUri: String?,
        callback: (Boolean, String) -> Unit
    ) {
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
                    createInventory(inventory, callback)
                } else {
                    callback(false, "Failed to upload image: $message")
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
            createInventory(inventory, callback)
        }
    }

    override fun getAllInventory(
        callback: (Boolean, String, List<InventoryModel>?) -> Unit
    ) {
        ref.orderByChild("timestamp")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val inventoryList = mutableListOf<InventoryModel>()
                        for (inventorySnapshot in snapshot.children) {
                            val inventory = inventorySnapshot.getValue(InventoryModel::class.java)
                            inventory?.let { inventoryList.add(it) }
                        }
                        // Sort by timestamp (newest first)
                        val sortedList = inventoryList.sortedByDescending { it.timestamp }
                        callback(true, "Inventory loaded successfully", sortedList)
                        Log.d("InventoryRepository", "Loaded ${inventoryList.size} inventory items")
                    } catch (e: Exception) {
                        Log.e("InventoryRepository", "Error parsing inventory", e)
                        callback(false, "Error parsing inventory: ${e.message}", null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("InventoryRepository", "Database error: ${error.message}")
                    callback(false, "Database error: ${error.message}", null)
                }
            })
    }

    override fun getInventoryByCurrentUser(
        callback: (Boolean, String, List<InventoryModel>?) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "User not authenticated", null)
            return
        }

        ref.orderByChild("userID").equalTo(currentUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val inventoryList = mutableListOf<InventoryModel>()
                        for (inventorySnapshot in snapshot.children) {
                            val inventory = inventorySnapshot.getValue(InventoryModel::class.java)
                            inventory?.let { inventoryList.add(it) }
                        }
                        // Sort by timestamp (newest first)
                        val sortedList = inventoryList.sortedByDescending { it.timestamp }
                        callback(true, "Inventory loaded successfully", sortedList)
                        Log.d("InventoryRepository", "Loaded ${inventoryList.size} user inventory items")
                    } catch (e: Exception) {
                        Log.e("InventoryRepository", "Error parsing inventory", e)
                        callback(false, "Error parsing inventory: ${e.message}", null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("InventoryRepository", "Database error: ${error.message}")
                    callback(false, "Database error: ${error.message}", null)
                }
            })
    }

    override fun getInventoryById(
        inventoryID: String,
        callback: (Boolean, String, InventoryModel?) -> Unit
    ) {
        ref.child(inventoryID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val inventory = snapshot.getValue(InventoryModel::class.java)
                    if (inventory != null) {
                        callback(true, "Inventory loaded successfully", inventory)
                    } else {
                        callback(false, "Inventory not found", null)
                    }
                } catch (e: Exception) {
                    Log.e("InventoryRepository", "Error parsing inventory", e)
                    callback(false, "Error parsing inventory: ${e.message}", null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("InventoryRepository", "Database error: ${error.message}")
                callback(false, "Database error: ${error.message}", null)
            }
        })
    }

    override fun updateInventory(
        inventoryId: String,
        updatedInventory: InventoryModel,
        callback: (Boolean, String) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "User not authenticated")
            return
        }

        // Ensure the inventory belongs to current user
        if (updatedInventory.userID != currentUser.uid) {
            callback(false, "Unauthorized to edit this inventory")
            return
        }

        ref.child(inventoryId).setValue(updatedInventory)
            .addOnSuccessListener {
                Log.d("InventoryRepository", "Inventory updated successfully")
                callback(true, "Medicine updated successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("InventoryRepository", "Failed to update inventory", exception)
                callback(false, "Failed to update medicine: ${exception.message}")
            }
    }

    override fun deleteInventory(
        inventoryId: String,
        callback: (Boolean, String) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "User not authenticated")
            return
        }

        // First check if inventory belongs to current user
        ref.child(inventoryId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val inventory = snapshot.getValue(InventoryModel::class.java)
                if (inventory != null && inventory.userID == currentUser.uid) {
                    // Delete the inventory
                    ref.child(inventoryId).removeValue()
                        .addOnSuccessListener {
                            Log.d("InventoryRepository", "Inventory deleted successfully")
                            callback(true, "Medicine deleted successfully")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("InventoryRepository", "Failed to delete inventory", exception)
                            callback(false, "Failed to delete medicine: ${exception.message}")
                        }
                } else {
                    callback(false, "Unauthorized to delete this inventory")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("InventoryRepository", "Database error: ${error.message}")
                callback(false, "Database error: ${error.message}")
            }
        })
    }

    override fun searchInventory(
        query: String,
        callback: (Boolean, String, List<InventoryModel>?) -> Unit
    ) {
        getAllInventory { success, message, inventoryList ->
            if (success && inventoryList != null) {
                val filteredList = inventoryList.filter {
                    it.medicineName.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true)
                }
                callback(true, "Search completed", filteredList)
            } else {
                callback(false, message, null)
            }
        }
    }

    override fun listenToAllInventory(callback: (List<InventoryModel>) -> Unit) {
        ref.orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val inventoryList = mutableListOf<InventoryModel>()
                        for (inventorySnapshot in snapshot.children) {
                            val inventory = inventorySnapshot.getValue(InventoryModel::class.java)
                            inventory?.let { inventoryList.add(it) }
                        }
                        // Sort by timestamp (newest first)
                        val sortedList = inventoryList.sortedByDescending { it.timestamp }
                        callback(sortedList)
                        Log.d("InventoryRepository", "Real-time inventory updated: ${inventoryList.size}")
                    } catch (e: Exception) {
                        Log.e("InventoryRepository", "Error in real-time listener", e)
                        callback(emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("InventoryRepository", "Real-time listener cancelled: ${error.message}")
                    callback(emptyList())
                }
            })
    }

    override fun listenToUserInventory(
        userID: String,
        callback: (List<InventoryModel>) -> Unit
    ) {
        ref.orderByChild("userID").equalTo(userID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val inventoryList = mutableListOf<InventoryModel>()
                        for (inventorySnapshot in snapshot.children) {
                            val inventory = inventorySnapshot.getValue(InventoryModel::class.java)
                            inventory?.let { inventoryList.add(it) }
                        }
                        // Sort by timestamp (newest first)
                        val sortedList = inventoryList.sortedByDescending { it.timestamp }
                        callback(sortedList)
                        Log.d("InventoryRepository", "Real-time user inventory updated: ${inventoryList.size}")
                    } catch (e: Exception) {
                        Log.e("InventoryRepository", "Error in real-time listener", e)
                        callback(emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("InventoryRepository", "Real-time listener cancelled: ${error.message}")
                    callback(emptyList())
                }
            })
    }

    private var inventoryListener: ValueEventListener? = null

    override fun stopListening() {
        inventoryListener?.let {
            ref.removeEventListener(it)
            inventoryListener = null
            Log.d("InventoryRepository", "Stopped listening to inventory updates")
        }
    }
}
