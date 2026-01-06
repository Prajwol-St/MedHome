package com.example.medhomeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medhomeapp.model.TimeSlot
import com.example.medhomeapp.model.UserModel
import com.example.medhomeapp.repository.AppointmentBookingRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppointmentBookingViewModel(
    private val repo: AppointmentBookingRepo
    ) : ViewModel() {

        private val _bookingState = MutableStateFlow<Pair<Boolean?, String?>>(
            null to null
        )
        val bookingState: StateFlow<Pair<Boolean?, String?>> = _bookingState

        fun book(
            slot: TimeSlot,
            patient: UserModel,
            reason: String
        ) {
            repo.bookAppointment(slot, patient, reason) { success, msg ->
                _bookingState.value = success to msg
            }
        }
    }