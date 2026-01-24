package com.example.medhomeapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.OrderModel
import com.example.medhomeapp.repository.OrderRepo

class OrderViewModel(private val repo: OrderRepo) : ViewModel() {

    // LiveData for loading state
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    // LiveData for order creation/update status
    private val _orderStatus = MutableLiveData<Pair<Boolean, String>>()
    val orderStatus: LiveData<Pair<Boolean, String>> get() = _orderStatus

    // LiveData for all orders
    private val _allOrders = MutableLiveData<List<OrderModel>>()
    val allOrders: LiveData<List<OrderModel>> get() = _allOrders

    // LiveData for single order
    private val _currentOrder = MutableLiveData<OrderModel?>()
    val currentOrder: LiveData<OrderModel?> get() = _currentOrder

        fun createOrder(order: OrderModel) {
        _loading.postValue(true)
        repo.createOrder(order) { success, message ->
            _loading.postValue(false)
            _orderStatus.postValue(Pair(success, message))
            if (success) {
                // Refresh order list after creation
                getCurrentUserOrders()
            }
            Log.d("OrderViewModel", "Order creation: $success - $message")
        }
    }

        fun uploadOrderProofImage(imageUri: String, callback: (Boolean, String, String?) -> Unit) {
        _loading.postValue(true)
        repo.uploadOrderProofImage(imageUri) { success, message, imageUrl ->
            _loading.postValue(false)
            callback(success, message, imageUrl)
            Log.d("OrderViewModel", "Image upload: $success - $message")
        }
    }

        fun createOrderWithProof(
        order: OrderModel,
        proofImageUri: String?
    ) {
        if (order.medicineName.isBlank() || order.deliveryAddress.isBlank() || order.phoneNumber.isBlank()) {
            _orderStatus.postValue(Pair(false, "Please fill all required fields"))
            return
        }

        if (proofImageUri != null) {
            // First upload proof image, then create order
            uploadOrderProofImage(proofImageUri) { success, message, uploadedImageUrl ->
                if (success && uploadedImageUrl != null) {
                    val orderWithProof = order.copy(
                        imageUrl = uploadedImageUrl
                    )
                    createOrder(orderWithProof)
                } else {
                    _orderStatus.postValue(Pair(false, "Failed to upload proof: $message"))
                }
            }
        } else {
            // Create order without proof image
            createOrder(order)
        }
    }

        fun getAllOrders() {
        _loading.postValue(true)
        repo.getAllOrders { success, message, orderList ->
            _loading.postValue(false)
            if (success && orderList != null) {
                _allOrders.postValue(orderList)
            } else {
                _allOrders.postValue(emptyList())
            }
            Log.d("OrderViewModel", "All orders: $success - $message")
        }
    }

        fun getCurrentUserOrders() {
        _loading.postValue(true)
        repo.getOrdersByCurrentUser { success, message, orderList ->
            _loading.postValue(false)
            if (success && orderList != null) {
                _allOrders.postValue(orderList)
            } else {
                _allOrders.postValue(emptyList())
            }
            Log.d("OrderViewModel", "Current user orders: $success - $message")
        }
    }

        fun getOrdersByUserId(userId: String) {
        _loading.postValue(true)
        repo.getOrdersByUserId(userId) { success, message, orderList ->
            _loading.postValue(false)
            if (success && orderList != null) {
                _allOrders.postValue(orderList)
            } else {
                _allOrders.postValue(emptyList())
            }
            Log.d("OrderViewModel", "Orders by user: $success - $message")
        }
    }

        fun getOrderById(orderId: String) {
        _loading.postValue(true)
        repo.getOrderById(orderId) { success, message, order ->
            _loading.postValue(false)
            if (success && order != null) {
                _currentOrder.postValue(order)
            } else {
                _currentOrder.postValue(null)
            }
            Log.d("OrderViewModel", "Get order: $success - $message")
        }
    }

        fun getOrdersByStatus(status: String) {
        _loading.postValue(true)
        repo.getOrdersByStatus(status) { success, message, orderList ->
            _loading.postValue(false)
            if (success && orderList != null) {
                _allOrders.postValue(orderList)
            } else {
                _allOrders.postValue(emptyList())
            }
            Log.d("OrderViewModel", "Orders by status: $success - $message")
        }
    }

        fun updateOrder(orderId: String, updatedOrder: OrderModel) {
        _loading.postValue(true)
        repo.updateOrder(orderId, updatedOrder) { success, message ->
            _loading.postValue(false)
            _orderStatus.postValue(Pair(success, message))
            if (success) {
                // Refresh orders after update
                getCurrentUserOrders()
            }
            Log.d("OrderViewModel", "Order update: $success - $message")
        }
    }

        fun updateOrderStatus(orderId: String, status: String) {
        _loading.postValue(true)
        repo.updateOrderStatus(orderId, status) { success, message ->
            _loading.postValue(false)
            _orderStatus.postValue(Pair(success, message))
            if (success) {
                // Refresh orders after status update
                getAllOrders()
            }
            Log.d("OrderViewModel", "Order status update: $success - $message")
        }
    }

        fun deleteOrder(orderId: String) {
        _loading.postValue(true)
        repo.deleteOrder(orderId) { success, message ->
            _loading.postValue(false)
            _orderStatus.postValue(Pair(success, message))
            if (success) {
                // Refresh orders after deletion
                getCurrentUserOrders()
            }
            Log.d("OrderViewModel", "Order deletion: $success - $message")
        }
    }

        fun cancelOrder(orderId: String) {
        _loading.postValue(true)
        repo.cancelOrder(orderId) { success, message ->
            _loading.postValue(false)
            _orderStatus.postValue(Pair(success, message))
            if (success) {
                // Refresh orders after cancellation
                getCurrentUserOrders()
            }
            Log.d("OrderViewModel", "Order cancellation: $success - $message")
        }
    }

        fun searchOrders(query: String) {
        _loading.postValue(true)
        repo.searchOrders(query) { success, message, orderList ->
            _loading.postValue(false)
            if (success && orderList != null) {
                _allOrders.postValue(orderList)
            } else {
                _allOrders.postValue(emptyList())
            }
            Log.d("OrderViewModel", "Search orders: $success - $message")
        }
    }

        fun listenToAllOrders() {
        repo.listenToAllOrders { orderList ->
            _allOrders.postValue(orderList)
            Log.d("OrderViewModel", "Real-time orders update: ${orderList.size} items")
        }
    }

        fun listenToUserOrders(userId: String) {
        repo.listenToUserOrders(userId) { orderList ->
            _allOrders.postValue(orderList)
            Log.d("OrderViewModel", "Real-time user orders update: ${orderList.size} items")
        }
    }

        fun listenToOrdersByStatus(status: String) {
        repo.listenToOrdersByStatus(status) { orderList ->
            _allOrders.postValue(orderList)
            Log.d("OrderViewModel", "Real-time orders by status update: ${orderList.size} items")
        }
    }

        fun stopListening() {
        repo.stopListening()
        Log.d("OrderViewModel", "Stopped listening to order updates")
    }

        fun clearStatus() {
        _orderStatus.postValue(Pair(false, ""))
    }

        fun clearCurrentOrder() {
        _currentOrder.postValue(null)
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}
