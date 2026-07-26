package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_reports")
data class ScanReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String,
    val timestamp: Long = System.currentTimeMillis(),
    val estAge: Int,
    val gender: String,
    val estBmi: Double,
    val heartRate: Int,
    val respiratoryRate: Int,
    val stressScore: Int,
    val fatigueScore: Int,
    val skinAcne: Int,
    val skinWrinkles: Int,
    val skinDarkCircles: Int,
    val skinPigmentation: Int,
    val skinHydration: Int,
    val skinOiliness: Int,
    val eyeRedness: Int,
    val eyeBlinkRate: Int,
    val eyeDrowsiness: Int,
    val stressLevel: String,
    val emotion: String,
    val anxietyIndicator: Int,
    val bloodPressure: String,
    val hrv: Int,
    val spo2: Int,
    val overallWellnessScore: Int,
    val wellnessCategory: String,
    val confidenceScore: Double,
    val notes: String = ""
)
