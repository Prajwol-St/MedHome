package com.example.medhomeapp.repository

import com.example.medhomeapp.model.OrderModel

interface OrderRepo {
    // Create operations
    fun createOrder(
        order: OrderModel,
        callback: (Boolean, String) -> Unit
    )

    fun uploadOrderProofImage(
        imageUri: String,
        callback: (Boolean, String, String?) -> Unit // success, message, imageUrl
    )

    // Read operations
    fun getAllOrders(
        callback: (Boolean, String, List<OrderModel>?) -> Unit
    )

    fun getOrdersByCurrentUser(
        callback: (Boolean, String, List<OrderModel>?) -> Unit
    )

    fun getOrdersByUserId(
        userId: String,
        callback: (Boolean, String, List<OrderModel>?) -> Unit
    )

    fun getOrderById(
        orderId: String,
        callback: (Boolean, String, OrderModel?) -> Unit
    )

    fun getOrdersByStatus(
        status: String,
        callback: (Boolean, String, List<OrderModel>?) -> Unit
    )

    // Update operations
    fun updateOrder(
        orderId: String,
        updatedOrder: OrderModel,
        callback: (Boolean, String) -> Unit
    )

    fun updateOrderStatus(
        orderId: String,
        status: String,
        callback: (Boolean, String) -> Unit
    )

    // Delete operations
    fun deleteOrder(
        orderId: String,
        callback: (Boolean, String) -> Unit
    )

    fun cancelOrder(
        orderId: String,
        callback: (Boolean, String) -> Unit
    )

    // Search operations
    fun searchOrders(
        query: String,
        callback: (Boolean, String, List<OrderModel>?) -> Unit
    )

    // Real-time listeners
    fun listenToAllOrders(
        callback: (List<OrderModel>) -> Unit
    )

    fun listenToUserOrders(
        userId: String,
        callback: (List<OrderModel>) -> Unit
    )

    fun listenToOrdersByStatus(
        status: String,
        callback: (List<OrderModel>) -> Unit
    )

    fun stopListening()
}
