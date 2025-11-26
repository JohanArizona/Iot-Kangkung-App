package com.example.dashboardhidroponikkangkung.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.example.dashboardhidroponikkangkung.data.model.*
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class HidroponikRepository {
    private val database = FirebaseDatabase.getInstance()
    private val rootRef = database.getReference("hidroponik")

    fun observeHidroponikData(): Flow<HidroponikData> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sensors = snapshot.child("sensors").let {
                    SensorData(
                        tds = it.child("tds").getValue(Int::class.java) ?: 0,
                        timestamp = it.child("timestamp").getValue(Long::class.java) ?: 0L
                    )
                }

                val pumpA = snapshot.child("pumps/pumpA").let {
                    PumpStatus(
                        status = it.child("status").getValue(Boolean::class.java) ?: false,
                        activationCount = it.child("activationCount").getValue(Long::class.java) ?: 0L,
                        lastActivation = it.child("lastActivation").getValue(Long::class.java) ?: 0L
                    )
                }

                val pumpB = snapshot.child("pumps/pumpB").let {
                    PumpStatus(
                        status = it.child("status").getValue(Boolean::class.java) ?: false,
                        activationCount = it.child("activationCount").getValue(Long::class.java) ?: 0L,
                        lastActivation = it.child("lastActivation").getValue(Long::class.java) ?: 0L
                    )
                }

                val isAutomatic = snapshot.child("mode/isAutomatic")
                    .getValue(Boolean::class.java) ?: true

                val history = mutableListOf<HistoryData>()
                snapshot.child("history").children.forEach { histSnapshot ->
                    history.add(
                        HistoryData(
                            tds = histSnapshot.child("tds").getValue(Int::class.java) ?: 0,
                            timestamp = histSnapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                        )
                    )
                }

                trySend(
                    HidroponikData(
                        sensors = sensors,
                        pumpA = pumpA,
                        pumpB = pumpB,
                        isAutomatic = isAutomatic,
                        history = history.sortedBy { it.timestamp }
                    )
                )
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        rootRef.addValueEventListener(listener)
        awaitClose { rootRef.removeEventListener(listener) }
    }

    // Mode Otomatis / Manual
    suspend fun setMode(isAutomatic: Boolean) {
        rootRef.child("mode/isAutomatic").setValue(isAutomatic).await()
    }

    // === INI YANG BARU: ON / OFF LANGSUNG ===
    suspend fun turnOnPump(pumpType: String) {
        val pumpRef = rootRef.child("pumps/pump$pumpType")
        val updates = mapOf(
            "status" to true,
            "lastActivation" to System.currentTimeMillis(),
            "activationCount" to ServerValue.increment(1)  // auto increment di server
        )
        pumpRef.updateChildren(updates).await()
    }

    suspend fun turnOffPump(pumpType: String) {
        val pumpRef = rootRef.child("pumps/pump$pumpType")
        val updates = mapOf(
            "status" to false,
            "lastActivation" to System.currentTimeMillis()
        )
        pumpRef.updateChildren(updates).await()
    }
}