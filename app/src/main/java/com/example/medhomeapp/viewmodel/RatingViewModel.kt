package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.RatingModel
import com.example.medhomeapp.repository.RatingRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RatingViewModel(
    private val repo: RatingRepo
) : ViewModel() {

    private val _rating = MutableStateFlow(0f)
    val rating: StateFlow<Float> = _rating.asStateFlow()

    private val _review = MutableStateFlow("")
    val review: StateFlow<String> = _review.asStateFlow()

    private val _canRate = MutableStateFlow(true)
    val canRate: StateFlow<Boolean> = _canRate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _operationResult = MutableStateFlow<Pair<Boolean, String>?>(null)
    val operationResult: StateFlow<Pair<Boolean, String>?> = _operationResult.asStateFlow()

    private val _doctorRatings = MutableStateFlow<List<RatingModel>>(emptyList())
    val doctorRatings: StateFlow<List<RatingModel>> = _doctorRatings.asStateFlow()

    fun setRating(value: Float) {
        _rating.value = value
    }

    fun setReview(text: String) {
        _review.value = text
    }

    fun checkIfAlreadyRated(appointmentId: String) {
        repo.hasPatientRatedAppointment(appointmentId) { alreadyRated ->
            _canRate.value = !alreadyRated
        }
    }

    fun submitRating(
        appointmentId: String,
        patientId: String,
        patientName: String,
        doctorId: String
    ) {
        if (_rating.value == 0f) {
            _operationResult.value = false to "Please select a rating"
            return
        }

        _isLoading.value = true

        val rating = RatingModel(
            appointmentId = appointmentId,
            patientId = patientId,
            patientName = patientName,
            doctorId = doctorId,
            rating = _rating.value,
            review = _review.value,
            createdAt = System.currentTimeMillis()
        )

        repo.addRating(rating) { success, message ->
            _isLoading.value = false
            _operationResult.value = success to message
        }
    }

    fun loadDoctorRatings(doctorId: String) {
        _isLoading.value = true
        repo.getRatingsByDoctor(doctorId) { ratings ->
            _doctorRatings.value = ratings
            _isLoading.value = false
        }
    }

    fun editRating(ratingId: String, newRating: Float, newReview: String) {
        _isLoading.value = true
        repo.editRating(ratingId, newRating, newReview) { success, message ->
            _isLoading.value = false
            _operationResult.value = success to message
        }
    }

    fun clearForm() {
        _rating.value = 0f
        _review.value = ""
    }

    fun clearOperationResult() {
        _operationResult.value = null
    }
}