package com.example.medhomeapp.repository

import com.example.medhomeapp.model.DoctorLeaveModel
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class LeaveManagementRepoImpl : LeaveManagementRepo {

    private val db = FirebaseDatabase.getInstance()
    private val leaveRef = db.getReference("doctorLeaves")

    override fun addLeave(
        leave: DoctorLeaveModel,
        callback: (Boolean, String) -> Unit
    ) {
        val leaveId = leaveRef.child(leave.doctorId).push().key

        if (leaveId == null) {
            callback(false, "Failed to generate leave ID")
            return
        }

        val leaveWithId = leave.copy(leaveId = leaveId)

        leaveRef.child(leave.doctorId)
            .child(leaveId)
            .setValue(leaveWithId.toMap())
            .addOnSuccessListener {
                callback(true, "Leave added successfully")
            }
            .addOnFailureListener {
                callback(false, "Failed to add leave: ${it.message}")
            }
    }

    override fun getLeaves(
        doctorId: String,
        callback: (List<DoctorLeaveModel>) -> Unit
    ) {
        leaveRef.child(doctorId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val leaves = snapshot.children.mapNotNull {
                        it.getValue(DoctorLeaveModel::class.java)
                    }
                    callback(leaves.sortedByDescending { it.startDate })
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    override fun deleteLeave(
        doctorId: String,
        leaveId: String,
        callback: (Boolean, String) -> Unit
    ) {
        leaveRef.child(doctorId)
            .child(leaveId)
            .removeValue()
            .addOnSuccessListener {
                callback(true, "Leave deleted successfully")
            }
            .addOnFailureListener {
                callback(false, "Failed to delete leave: ${it.message}")
            }
    }

    override fun getActiveLeaves(
        doctorId: String,
        callback: (List<DoctorLeaveModel>) -> Unit
    ) {
        leaveRef.child(doctorId)
            .orderByChild("isActive")
            .equalTo(true)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val leaves = snapshot.children.mapNotNull {
                        it.getValue(DoctorLeaveModel::class.java)
                    }.filter {
                        // Only return current or future leaves
                        !isDateInPast(it.endDate)
                    }
                    callback(leaves.sortedBy { it.startDate })
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    override fun isDoctorOnLeave(
        doctorId: String,
        date: String,
        callback: (Boolean) -> Unit
    ) {
        leaveRef.child(doctorId)
            .orderByChild("isActive")
            .equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val leaves = snapshot.children.mapNotNull {
                        it.getValue(DoctorLeaveModel::class.java)
                    }

                    val isOnLeave = leaves.any { leave ->
                        isDateInRange(date, leave.startDate, leave.endDate)
                    }

                    callback(isOnLeave)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }

    // Helper functions
    private fun isDateInPast(dateString: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(dateString) ?: return false
            date.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    private fun isDateInRange(checkDate: String, startDate: String, endDate: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val check = sdf.parse(checkDate) ?: return false
            val start = sdf.parse(startDate) ?: return false
            val end = sdf.parse(endDate) ?: return false

            !check.before(start) && !check.after(end)
        } catch (e: Exception) {
            false
        }
    }
}