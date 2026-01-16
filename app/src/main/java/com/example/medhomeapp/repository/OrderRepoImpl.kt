package com.example.medhomeapp.repository

import android.util.Log
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.medhomeapp.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.util.UUID

class OrderRepoImpl : OrderRepo {
    private val database = FirebaseDatabase.getInstance()
    private val ref = database.reference.child("orders")
    private val auth = FirebaseAuth.getInstance()

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dahtdixmf",
            "api_key" to "869547321896952",
            "api_secret" to "Xnq2hDhTGZXUZ-w3O1xUhjTIOoI",
        )
    )

    override fun createOrder(
        order: OrderModel,
        callback: (Boolean, String) -> Unit
    ) {
        try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e("OrderRepository", "User not authenticated")
                callback(false, "User not authenticated")
                return
            }

            val userId = currentUser.uid
            Log.d("OrderRepository", "Creating order for user: $userId")

            // Generate unique order ID
            val orderId = ref.push().key ?: UUID.randomUUID().toString()
            Log.d("OrderRepository", "Generated order ID: $orderId")

            // Create order with user data and initial status
            val orderData = order.copy(
                orderID = orderId,
                userID = userId,
                orderStatus = if (order.orderStatus.isEmpty()) "Pending" else order.orderStatus,
                timestamp = System.currentTimeMillis()
            )

            Log.d("OrderRepository", "Order data to save: $orderData")

            // Save to Firebase
            ref.child(orderId).setValue(orderData)
                .addOnSuccessListener {
                    Log.d("OrderRepository", "✅ Order created successfully with ID: $orderId")
                    callback(true, "Order placed successfully")
                }
                .addOnFailureListener { exception ->
                    Log.e("OrderRepository", "❌ Failed to create order", exception)
                    callback(false, "Failed to place order: ${exception.message}")
                }

        } catch (e: Exception) {
            Log.e("OrderRepository", "❌ Error creating order", e)
            callback(false, "Error placing order: ${e.message}")
        }
    }

    override fun uploadOrderProofImage(
        imageUri: String,
        callback: (Boolean, String, String?) -> Unit
    ) {
        try {
            Thread {
                try {
                    val file = File(imageUri)
                    if (!file.exists()) {
                        callback(false, "Image file not found", null)
                        return@Thread
                    }

                    val uploadResult = cloudinary.uploader().upload(
                        file,
                        ObjectUtils.asMap(
                            "folder", "orders",
                            "resource_type", "image",
                            "quality", "auto:good",
                            "fetch_format", "auto"
                        )
                    )

                    val imageUrl = uploadResult["secure_url"] as? String
                    if (imageUrl != null) {
                        Log.d("OrderRepo", "Order proof image uploaded successfully: $imageUrl")
                        callback(true, "Image uploaded successfully", imageUrl)
                    } else {
                        Log.e("OrderRepo", "Failed to get image URL from upload result")
                        callback(false, "Failed to get image URL", null)
                    }

                } catch (e: Exception) {
                    Log.e("OrderRepo", "Error uploading image", e)
                    callback(false, "Error uploading image: ${e.message}", null)
                }
            }.start()

        } catch (e: Exception) {
            Log.e("OrderRepository", "Error starting image upload", e)
            callback(false, "Error starting image upload: ${e.message}", null)
        }
    }

    override fun getAllOrders(
        callback: (Boolean, String, List<OrderModel>?) -> Unit
    ) {
        ref.orderByChild("timestamp")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val orderList = mutableListOf<OrderModel>()
                        for (orderSnapshot in snapshot.children) {
                            val order = orderSnapshot.getValue(OrderModel::class.java)
                            order?.let { orderList.add(it) }
                        }
                        val sortedList = orderList.sortedByDescending { it.timestamp }
                        callback(true, "Orders loaded successfully", sortedList)
                        Log.d("OrderRepository", "Loaded ${orderList.size} orders")
                    } catch (e: Exception) {
                        Log.e("OrderRepository", "Error parsing orders", e)
                        callback(false, "Error parsing orders: ${e.message}", null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("OrderRepository", "Database error: ${error.message}")
                    callback(false, "Database error: ${error.message}", null)
                }
            })
    }

    override fun getOrdersByCurrentUser(
        callback: (Boolean, String, List<OrderModel>?) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "User not authenticated", null)
            return
        }

        // ✅ FIXED: Changed from "userId" to "userID"
        ref.orderByChild("userID").equalTo(currentUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val orderList = mutableListOf<OrderModel>()
                        for (orderSnapshot in snapshot.children) {
                            val order = orderSnapshot.getValue(OrderModel::class.java)
                            order?.let { orderList.add(it) }
                        }
                        val sortedList = orderList.sortedByDescending { it.timestamp }
                        callback(true, "Orders loaded successfully", sortedList)
                        Log.d("OrderRepository", "Loaded ${orderList.size} user orders")
                    } catch (e: Exception) {
                        Log.e("OrderRepository", "Error parsing orders", e)
                        callback(false, "Error parsing orders: ${e.message}", null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("OrderRepository", "Database error: ${error.message}")
                    callback(false, "Database error: ${error.message}", null)
                }
            })
    }

    override fun getOrdersByUserId(
        userId: String,
        callback: (Boolean, String, List<OrderModel>?) -> Unit
    ) {
        // ✅ FIXED: Changed from "userId" to "userID"
        ref.orderByChild("userID").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val orderList = mutableListOf<OrderModel>()
                        for (orderSnapshot in snapshot.children) {
                            val order = orderSnapshot.getValue(OrderModel::class.java)
                            order?.let { orderList.add(it) }
                        }
                        val sortedList = orderList.sortedByDescending { it.timestamp }
                        callback(true, "Orders loaded successfully", sortedList)
                        Log.d("OrderRepository", "Loaded ${orderList.size} orders for user: $userId")
                    } catch (e: Exception) {
                        Log.e("OrderRepository", "Error parsing orders", e)
                        callback(false, "Error parsing orders: ${e.message}", null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("OrderRepository", "Database error: ${error.message}")
                    callback(false, "Database error: ${error.message}", null)
                }
            })
    }

    override fun getOrderById(
        orderId: String,
        callback: (Boolean, String, OrderModel?) -> Unit
    ) {
        ref.child(orderId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val order = snapshot.getValue(OrderModel::class.java)
                    if (order != null) {
                        callback(true, "Order loaded successfully", order)
                    } else {
                        callback(false, "Order not found", null)
                    }
                } catch (e: Exception) {
                    Log.e("OrderRepository", "Error parsing order", e)
                    callback(false, "Error parsing order: ${e.message}", null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OrderRepository", "Database error: ${error.message}")
                callback(false, "Database error: ${error.message}", null)
            }
        })
    }

    override fun getOrdersByStatus(
        status: String,
        callback: (Boolean, String, List<OrderModel>?) -> Unit
    ) {
        ref.orderByChild("orderStatus").equalTo(status)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val orderList = mutableListOf<OrderModel>()
                        for (orderSnapshot in snapshot.children) {
                            val order = orderSnapshot.getValue(OrderModel::class.java)
                            order?.let { orderList.add(it) }
                        }
                        val sortedList = orderList.sortedByDescending { it.timestamp }
                        callback(true, "Orders loaded successfully", sortedList)
                        Log.d("OrderRepository", "Loaded ${orderList.size} orders with status: $status")
                    } catch (e: Exception) {
                        Log.e("OrderRepository", "Error parsing orders", e)
                        callback(false, "Error parsing orders: ${e.message}", null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("OrderRepository", "Database error: ${error.message}")
                    callback(false, "Database error: ${error.message}", null)
                }
            })
    }

    override fun updateOrder(
        orderId: String,
        updatedOrder: OrderModel,
        callback: (Boolean, String) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "User not authenticated")
            return
        }

        ref.child(orderId).setValue(updatedOrder)
            .addOnSuccessListener {
                Log.d("OrderRepository", "Order updated successfully")
                callback(true, "Order updated successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("OrderRepository", "Failed to update order", exception)
                callback(false, "Failed to update order: ${exception.message}")
            }
    }

    override fun updateOrderStatus(
        orderId: String,
        status: String,
        callback: (Boolean, String) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "User not authenticated")
            return
        }

        val updates = mapOf(
            "orderStatus" to status
        )

        ref.child(orderId).updateChildren(updates)
            .addOnSuccessListener {
                Log.d("OrderRepository", "Order status updated to: $status")
                callback(true, "Order status updated successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("OrderRepository", "Failed to update order status", exception)
                callback(false, "Failed to update status: ${exception.message}")
            }
    }

    override fun deleteOrder(
        orderId: String,
        callback: (Boolean, String) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "User not authenticated")
            return
        }

        ref.child(orderId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(OrderModel::class.java)
                if (order != null) {
                    ref.child(orderId).removeValue()
                        .addOnSuccessListener {
                            Log.d("OrderRepository", "Order deleted successfully")
                            callback(true, "Order deleted successfully")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("OrderRepository", "Failed to delete order", exception)
                            callback(false, "Failed to delete order: ${exception.message}")
                        }
                } else {
                    callback(false, "Order not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OrderRepository", "Database error: ${error.message}")
                callback(false, "Database error: ${error.message}")
            }
        })
    }

    override fun cancelOrder(
        orderId: String,
        callback: (Boolean, String) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "User not authenticated")
            return
        }

        ref.child(orderId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(OrderModel::class.java)
                if (order != null && order.userID == currentUser.uid) {
                    if (order.orderStatus.equals("Completed", ignoreCase = true) ||
                        order.orderStatus.equals("Cancelled", ignoreCase = true)) {
                        callback(false, "Cannot cancel this order")
                        return
                    }

                    val updates = mapOf(
                        "orderStatus" to "Cancelled"
                    )

                    ref.child(orderId).updateChildren(updates)
                        .addOnSuccessListener {
                            Log.d("OrderRepository", "Order cancelled successfully")
                            callback(true, "Order cancelled successfully")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("OrderRepository", "Failed to cancel order", exception)
                            callback(false, "Failed to cancel order: ${exception.message}")
                        }
                } else {
                    callback(false, "Unauthorized to cancel this order")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OrderRepository", "Database error: ${error.message}")
                callback(false, "Database error: ${error.message}")
            }
        })
    }

    override fun searchOrders(
        query: String,
        callback: (Boolean, String, List<OrderModel>?) -> Unit
    ) {
        getAllOrders { success, message, orderList ->
            if (success && orderList != null) {
                val filteredList = orderList.filter {
                    it.orderID.contains(query, ignoreCase = true) ||
                            it.orderStatus.contains(query, ignoreCase = true) ||
                            it.deliveryAddress.contains(query, ignoreCase = true)
                }
                callback(true, "Search completed", filteredList)
            } else {
                callback(false, message, null)
            }
        }
    }

    override fun listenToAllOrders(callback: (List<OrderModel>) -> Unit) {
        ref.orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val orderList = mutableListOf<OrderModel>()
                        for (orderSnapshot in snapshot.children) {
                            val order = orderSnapshot.getValue(OrderModel::class.java)
                            order?.let { orderList.add(it) }
                        }
                        val sortedList = orderList.sortedByDescending { it.timestamp }
                        callback(sortedList)
                        Log.d("OrderRepository", "Real-time orders updated: ${orderList.size}")
                    } catch (e: Exception) {
                        Log.e("OrderRepository", "Error in real-time listener", e)
                        callback(emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("OrderRepository", "Real-time listener cancelled: ${error.message}")
                    callback(emptyList())
                }
            })
    }

    override fun listenToUserOrders(
        userID: String,
        callback: (List<OrderModel>) -> Unit
    ) {
        // ✅ FIXED: Changed from "userId" to "userID"
        Log.d("OrderRepository", "Setting up listener for user: $userID")

        ref.orderByChild("userID").equalTo(userID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        Log.d("OrderRepository", "Data snapshot received: ${snapshot.childrenCount} children")

                        val orderList = mutableListOf<OrderModel>()
                        for (orderSnapshot in snapshot.children) {
                            val order = orderSnapshot.getValue(OrderModel::class.java)
                            if (order != null) {
                                Log.d("OrderRepository", "Order found: ${order.orderID} - ${order.medicineName}")
                                orderList.add(order)
                            }
                        }
                        val sortedList = orderList.sortedByDescending { it.timestamp }
                        callback(sortedList)
                        Log.d("OrderRepository", "✅ Real-time user orders updated: ${orderList.size}")
                    } catch (e: Exception) {
                        Log.e("OrderRepository", "❌ Error in real-time listener", e)
                        callback(emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("OrderRepository", "❌ Real-time listener cancelled: ${error.message}")
                    callback(emptyList())
                }
            })
    }

    override fun listenToOrdersByStatus(
        status: String,
        callback: (List<OrderModel>) -> Unit
    ) {
        ref.orderByChild("orderStatus").equalTo(status)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val orderList = mutableListOf<OrderModel>()
                        for (orderSnapshot in snapshot.children) {
                            val order = orderSnapshot.getValue(OrderModel::class.java)
                            order?.let { orderList.add(it) }
                        }
                        val sortedList = orderList.sortedByDescending { it.timestamp }
                        callback(sortedList)
                        Log.d("OrderRepository", "Real-time orders by status updated: ${orderList.size}")
                    } catch (e: Exception) {
                        Log.e("OrderRepository", "Error in real-time listener", e)
                        callback(emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("OrderRepository", "Real-time listener cancelled: ${error.message}")
                    callback(emptyList())
                }
            })
    }

    private var orderListener: ValueEventListener? = null

    override fun stopListening() {
        orderListener?.let {
            ref.removeEventListener(it)
            orderListener = null
            Log.d("OrderRepository", "Stopped listening to order updates")
        }
    }
}
