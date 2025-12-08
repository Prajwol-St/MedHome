package com.example.medhomeapp.repository

import com.example.medhomeapp.model.DoctorModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DoctorRepoImpl: DoctorRepo {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("Doctor")


    override fun addDoctor(
        doctor: DoctorModel,
        callback: (Boolean, String) -> Unit
    ) {
        val doctorId = doctor.id.toString()
        ref.child(doctorId).setValue(doctor)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Doctor added successfully")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun getDoctorByUserId(
        userId: String,
        callback: (Boolean, String, DoctorModel?) -> Unit
    ) {
        ref.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    callback(false, "Doctor not found", null)
                    return
                }

                val doctor = snapshot.getValue(DoctorModel::class.java)
                callback(true, "Doctor fetched", doctor)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun getAllDoctors(
        callback: (Boolean, String, List<DoctorModel>) -> Unit
    ) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    callback(true, "No doctors found", emptyList())
                    return
                }

                val allDoctors = mutableListOf<DoctorModel>()

                for (data in snapshot.children) {
                    val doctor = data.getValue(DoctorModel::class.java)
                    if (doctor != null) allDoctors.add(doctor)
                }

                callback(true, "Doctors fetched", allDoctors)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun editDoctorProfile(
        userId: String,
        model: DoctorModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId)
            .updateChildren(model.toMap())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Doctor profile updated")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun deleteDoctor(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Doctor deleted")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }
}