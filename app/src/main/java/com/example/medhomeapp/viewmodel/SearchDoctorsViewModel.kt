package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.DoctorModel
import com.example.medhomeapp.model.DoctorSearchFilterModel
import com.example.medhomeapp.repository.DoctorRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchDoctorsViewModel(
    private val repo: DoctorRepo
) : ViewModel() {

    private val _allDoctors = MutableStateFlow<List<DoctorModel>>(emptyList())
    val allDoctors: StateFlow<List<DoctorModel>> = _allDoctors.asStateFlow()

    private val _filteredDoctors = MutableStateFlow<List<DoctorModel>>(emptyList())
    val filteredDoctors: StateFlow<List<DoctorModel>> = _filteredDoctors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentFilter = MutableStateFlow(DoctorSearchFilterModel())
    val currentFilter: StateFlow<DoctorSearchFilterModel> = _currentFilter.asStateFlow()

    init {
        loadDoctors()
    }

    fun loadDoctors() {
        _isLoading.value = true
        repo.getAllDoctors { success, message, doctors ->
            if (success) {
                _allDoctors.value = doctors
                _filteredDoctors.value = doctors
            }
            _isLoading.value = false
        }
    }

    fun searchDoctors(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun updateFilter(filter: DoctorSearchFilterModel) {
        _currentFilter.value = filter
        applyFilters()
    }

    fun filterBySpecialization(specialization: String) {
        val newFilter = _currentFilter.value.copy(specialization = specialization)
        _currentFilter.value = newFilter
        applyFilters()
    }

    fun filterByRating(minRating: Float) {
        val newFilter = _currentFilter.value.copy(minRating = minRating)
        _currentFilter.value = newFilter
        applyFilters()
    }

    fun filterByFeeRange(minFee: Double, maxFee: Double) {
        val newFilter = _currentFilter.value.copy(minFee = minFee, maxFee = maxFee)
        _currentFilter.value = newFilter
        applyFilters()
    }

    fun sortDoctors(sortBy: String) {
        val newFilter = _currentFilter.value.copy(sortBy = sortBy)
        _currentFilter.value = newFilter
        applyFilters()
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _currentFilter.value = DoctorSearchFilterModel()
        _filteredDoctors.value = _allDoctors.value
    }

    private fun applyFilters() {
        var filtered = _allDoctors.value

        if (_searchQuery.value.isNotBlank()) {
            filtered = filtered.filter { doctor ->
                doctor.name.contains(_searchQuery.value, ignoreCase = true) ||
                        doctor.specialization.contains(_searchQuery.value, ignoreCase = true)
            }
        }

        if (_currentFilter.value.specialization.isNotBlank()) {
            filtered = filtered.filter {
                it.specialization == _currentFilter.value.specialization
            }
        }

        if (_currentFilter.value.minRating > 0) {
            filtered = filtered.filter {
                it.averageRating >= _currentFilter.value.minRating
            }
        }

        filtered = filtered.filter { doctor ->
            doctor.consultationFee >= _currentFilter.value.minFee &&
                    doctor.consultationFee <= _currentFilter.value.maxFee
        }


        filtered = when (_currentFilter.value.sortBy) {
            "rating_desc" -> filtered.sortedByDescending { it.averageRating }
            "rating_asc" -> filtered.sortedBy { it.averageRating }
            "fee_asc" -> filtered.sortedBy { it.consultationFee }
            "fee_desc" -> filtered.sortedByDescending { it.consultationFee }
            "experience_desc" -> filtered.sortedByDescending { it.experience }
            else -> filtered
        }

        _filteredDoctors.value = filtered
    }

    fun getSpecializations(): List<String> {
        return _allDoctors.value
            .map { it.specialization }
            .distinct()
            .sorted()
    }
}